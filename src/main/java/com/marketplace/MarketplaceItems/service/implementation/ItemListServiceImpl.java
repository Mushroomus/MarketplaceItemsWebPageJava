package com.marketplace.MarketplaceItems.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.MarketplaceItems.dao.ItemListDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.ItemList;
import com.marketplace.MarketplaceItems.entity.ListDetails;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.model.*;
import com.marketplace.MarketplaceItems.service.ItemListService;
import com.marketplace.MarketplaceItems.service.operation.ItemListAndItemOperations;
import com.marketplace.MarketplaceItems.service.operation.ItemListAndListDetailsOperations;
import com.marketplace.MarketplaceItems.service.operation.ItemListUserOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemListServiceImpl implements ItemListService {
    private ItemListDAO itemListDAO;
    private ItemListAndItemOperations itemListAndItemOperations;
    private ItemListUserOperations itemListUserOperations;
    private ItemListAndListDetailsOperations itemListAndListDetailsOperations;

    @Autowired
    public ItemListServiceImpl(ItemListDAO theItemListDAO, ItemListAndItemOperations theItemListAndItemOperations, ItemListUserOperations theItemListUserOperations, ItemListAndListDetailsOperations theItemListAndListDetailsOperations) {
        itemListDAO = theItemListDAO;
        itemListAndItemOperations = theItemListAndItemOperations;
        itemListUserOperations = theItemListUserOperations;
        itemListAndListDetailsOperations = theItemListAndListDetailsOperations;
    }


    @Override
    public Map<String, Object> getListInfoAndButtonStatus() {
        User user = itemListUserOperations.getCurrentUser();
        List<User.ListInfoModel> listInfo = user.getListNamesWithItemCount();
        List<ItemList> lists = itemListDAO.findByUserId(user.getId());
        int uniqueListNames = lists.stream()
                .map(itemList -> itemList.getList().getName())
                .distinct()
                .collect(Collectors.toList()).size();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("listInfo", listInfo);

        if(uniqueListNames == 0) {
            resultMap.put("redirect", true);
        } else {
            resultMap.put("disableCreateButton", uniqueListNames >= 3);
        }
        return resultMap;
    }

    @Override
    public void deleteItemList(String name) {
        ListDetails foundList = itemListAndListDetailsOperations.findListByName(name);
        if (foundList != null) {
            List<ItemList> itemsList = itemListDAO.findByListId(foundList.getId());
            itemListDAO.deleteAll(itemsList);
            itemListAndListDetailsOperations.deleteList(foundList);
        } else {
            throw new IllegalArgumentException("List not found");
        }
    }

    @Override
    public Map<String, Object> getCreateListTableData() {
        Map<String, Object> model = new HashMap<>();

        User user = itemListUserOperations.getCurrentUser();
        List<ItemList> lists = itemListDAO.findByUserId(user.getId());

        List<String> listNames = lists.stream()
                .map(x -> x.getList().getName())
                .collect(Collectors.toList());

        List<String> classesList = Arrays.asList("Multi-class", "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        List<String> qualityList = Arrays.asList("Genuine", "Vintage", "Unique", "Strange", "Haunted");
        List<String> typeList = Arrays.asList("Cosmetics", "Currencies", "Tools", "Paints", "Action", "Weapons", "Strange Parts", "Botkillers", "Festive Weapons", "Halloween");

        model.put("listNames", String.join(",", listNames).replace("[", "").replace("]", "").replace("\"", ""));
        model.put("craftableOptions", Arrays.asList("Any", "Yes", "No"));
        model.put("classesList", classesList);
        model.put("qualityList", qualityList);
        model.put("typeList", typeList);

        return model;
    }

    @Override
    public ResponseEntity<ResponseMessage> createList(CreateListRequest request) {

        String username = request.getUsername();
        List<String> itemSku = request.getItemSku();
        String listName = request.getListName();

        User user = itemListUserOperations.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(new ResponseMessage("User not found"), HttpStatus.BAD_REQUEST);
        }

        ListDetails listDetails = new ListDetails();
        listDetails.setName(listName);
        listDetails.setDate(LocalDateTime.now());
        listDetails.setUser(user);
        itemListAndListDetailsOperations.saveList(listDetails);


        for (String theItemSku : itemSku) {

            Item item = itemListAndItemOperations.findItemBySku(theItemSku);

            if (item == null) {
                return new ResponseEntity<>(new ResponseMessage("Item not found"), HttpStatus.BAD_REQUEST);
            }

            ItemList itemList = ItemList.builder()
                    .item(item)
                    .user(user)
                    .list(listDetails)
                    .build();
            itemListDAO.save(itemList);
        }

        return new ResponseEntity<>(new ResponseMessage("List was created"), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResponseMessage> saveEditedList(EditListRequest request) {
        try {
            User user = itemListUserOperations.getCurrentUser();
            ListDetails listDetails = itemListAndListDetailsOperations.findListByName(request.getListName());
            List<ItemList> items = itemListDAO.findByUserIdAndListId(user.getId(), listDetails.getId());

            itemListDAO.deleteAll(items);

            ItemList itemList;
            Item itemToAdd;

            for(String itemSku : request.getItemSku()) {

                itemToAdd = itemListAndItemOperations.findItemBySku(itemSku);
                itemList = ItemList.builder()
                        .item(itemToAdd)
                        .user(user)
                        .list(listDetails)
                        .build();
                itemListDAO.save(itemList);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Something went wrong"), HttpStatus.valueOf(404));
        }
        return new ResponseEntity<>(new ResponseMessage("List was edited"), HttpStatus.valueOf(200));
    }

    @Override
    public ResponseEntity<List<Item>> fetchRightList(@RequestParam(value="listName") String name)
    {
        User user = itemListUserOperations.getCurrentUser();

        ListDetails listDetails = itemListAndListDetailsOperations.findListByName(name);

        List<ItemList> results = itemListDAO.findByUserIdAndListId(user.getId(), listDetails.getId());

        List<Item> items = new ArrayList<>();

        for(ItemList record : results) {
            items.add( record.getItem() );
        }

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @Override
    public List<ItemRequestResult> priceItems(String name, String marketplaceKeyPrice) {

        User user = itemListUserOperations.getCurrentUser();
        ListDetails listDetails = itemListAndListDetailsOperations.findListByNameAndUser(name, user);

        java.util.List<ItemList> results = itemListDAO.findByUserIdAndListId(user.getId(), listDetails.getId());

        java.util.List<String> apiRequests = results.stream().map(itemList-> "https://api2.prices.tf/prices/" + itemList.getItem().getSku())
                .collect(Collectors.toList());

        String[] apiRequestsArray = apiRequests.toArray(new String[apiRequests.size()]);

        WebClient client = WebClient.builder().baseUrl("https://api2.prices.tf").build();

        String accessToken = client.post()
                .uri("/auth/access")
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block()
                .getAccessToken();

        Mono<ItemRequest> keyPriceMono = client.get()
                .uri("https://api2.prices.tf/prices/5021;6")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(String.class)
                .retryBackoff(3, Duration.ofSeconds(1))
                .map(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.readValue(response, ItemRequest.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize response into ItemRequest", e);
                    }
                });

        Mono<java.util.List<ItemRequest>> itemRequestsMono = Flux
                .fromArray(apiRequestsArray)
                .flatMap(path -> client.get()
                        .uri(path)
                        .headers(headers -> headers.setBearerAuth(accessToken))
                        .retrieve()
                        .bodyToMono(String.class)
                        .retryBackoff(3, Duration.ofSeconds(1))
                )
                .map(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.readValue(response, ItemRequest.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize response into ItemRequest", e);
                    }
                })
                .collect(Collectors.toList());

        ItemRequest keyPrice = keyPriceMono.block();
        List<ItemRequest> itemRequests = itemRequestsMono.block();

        Double keyPriceFormatted = (double) keyPrice.getSellHalfScrap() / 18;

        List<ItemRequestResult> resultList = new ArrayList<>();
        Item item;
        Double metal;

        for(ItemRequest req : itemRequests) {
            ItemList itemSearch = results.stream()
                    .filter(record -> record.getItem().getSku().equals(req.getSku()))
                    .findFirst()
                    .orElse(null);
            item = itemSearch.getItem();

            metal =  Double.valueOf(req.getSellHalfScrap()) / 18;
            Double calculatedProfit;

            Double marketplacePrice = item.getMarketplacePrice();

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String formattedMetal = decimalFormat.format(metal);

            String backpackPrice = formattedMetal + " refs";

            Integer amountKey = req.getSellKeys();
            if(amountKey == 0) {
                calculatedProfit = keyPriceFormatted / metal;

                calculatedProfit = calculatedProfit * marketplacePrice;
                calculatedProfit = calculatedProfit - (calculatedProfit * 0.1);
                calculatedProfit = calculatedProfit - Double.parseDouble(marketplaceKeyPrice);
            } else {
                calculatedProfit = amountKey + (metal / keyPriceFormatted);

                if(amountKey == 1)
                    backpackPrice = amountKey + " key " + backpackPrice;
                else
                    backpackPrice = amountKey + " keys " + backpackPrice;

                calculatedProfit = Double.parseDouble(marketplaceKeyPrice) * calculatedProfit;
                calculatedProfit = (marketplacePrice - marketplacePrice * 0.1) - calculatedProfit;
            }
            resultList.add( new ItemRequestResult(item.getSku(), item.getName(), Double.toString(item.getMarketplacePrice()) + '$', backpackPrice, decimalFormat.format(calculatedProfit)));
        }

        return resultList;
    }

    @Override
    public ResponseEntity<PagedModel<Item>> getItemList(int page, int size, String search, String craftable, List<String> classes, List<String> qualities, List<String> types)
    {
        return itemListAndItemOperations.getItems(page, size, search, craftable, classes, qualities, types);
    }
}
