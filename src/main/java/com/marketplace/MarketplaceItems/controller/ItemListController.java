package com.marketplace.MarketplaceItems.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.MarketplaceItems.dao.ItemListDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.ItemList;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.entity.List;
import com.marketplace.MarketplaceItems.service.ItemListService;
import com.marketplace.MarketplaceItems.service.ItemService;
import com.marketplace.MarketplaceItems.service.UserService;
import com.marketplace.MarketplaceItems.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping("/lists")
public class ItemListController {

    //@Autowired
   // private PagedResourcesAssembler<Item> assembler;

    private ItemService itemService;
    private UserService userService;

    private ListService listService;

    private ItemListService itemListService;
    private final ItemListDAO itemListDAO;

    public ItemListController(ItemService theItemService, UserService theUserService, ListService theListService, ItemListService theItemListService,
                              ItemListDAO itemListDAO) {

        itemService = theItemService;
        userService = theUserService;
        listService = theListService;
        itemListService = theItemListService;
        this.itemListDAO = itemListDAO;
    }


    public static class CreateListRequest {
        private String username;
        private java.util.List<String> itemSku;
        private String listName;

        public CreateListRequest() { };

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public java.util.List<String> getItemSku() {
            return itemSku;
        }

        public void setItemSku(java.util.List<String> itemSku) {
            this.itemSku = itemSku;
        }

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }
    }


    @GetMapping("/show")
    public String showLists(Model theModel) {
        User user = userService.getCurrentUser();
        java.util.List<User.ListInfoModel> listInfo = user.getListNamesWithItemCount();

        theModel.addAttribute("listInfo", listInfo);
        return "lists/show-list-items";
    }

    @GetMapping("/delete")
    public String deleteList(@RequestParam(value = "listName") String name) {

        List foundList = listService.findListByName(name);
        System.out.println(foundList.getName());

        if ( foundList != null ) {

            java.util.List<ItemList> itemsList = itemListService.findByListId(foundList.getId());

            itemListDAO.deleteAll(itemsList);
            listService.deleteList(foundList);
        }

        return "redirect:/lists/show";
    }

    @GetMapping("/create-list")
    public String createTable() {

        return "lists/create-list-items";
    }

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<UserController.ResponseMessage>
    createList(@RequestBody CreateListRequest request) {

        String username = request.getUsername();
        java.util.List<String> itemSku = request.getItemSku();
        String listName = request.getListName();

        User user = userService.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(new UserController.ResponseMessage("User not found"), HttpStatus.BAD_REQUEST);
        }

        List list = new List();
        list.setName(listName);
        list.setDate(LocalDateTime.now());
        listService.saveList(list);


        for (String theItemSku : itemSku) {

            Item item = itemService.findItemBySku(theItemSku);

            if (item == null) {
                return new ResponseEntity<>(new UserController.ResponseMessage("Item not found"), HttpStatus.BAD_REQUEST);
            }

            ItemList itemList = new ItemList(item,user,list);
            itemListService.saveItemList(itemList);
        }

        return new ResponseEntity<>(new UserController.ResponseMessage("List was created"), HttpStatus.CREATED);
    }



    @GetMapping(value= "/fetch-list")
    public ResponseEntity<PagedModel<Item>>
    fetchList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "5") int size,
                                                      @RequestParam(defaultValue="") String search,
                                                      @RequestParam(defaultValue = "") String craftable,
                                                      @RequestParam(defaultValue = "") java.util.List<String> classes,
                                                      @RequestParam(defaultValue = "") java.util.List<String> qualities,
                                                      @RequestParam(defaultValue = "") java.util.List<String> types)
    {

        Page<Item> items;
        Pageable pageable = PageRequest.of(page, 5);

        items = itemService.findAllFilters(pageable, search, craftable, classes, qualities, types);

        PagedModel<Item> pagedModel = PagedModel.of(items.getContent(), new PagedModel.PageMetadata(items.getSize(), items.getNumber(), items.getTotalElements()));

        System.out.println("Paged Model " + pagedModel);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);

    }

    @GetMapping(value= "/edit")
    public String editList(@RequestParam(value="listName") String name, Model theModel)
    {
        theModel.addAttribute("listName", name);
        return "lists/edit-list-items";
    }

    public static class EditListRequest {

        private java.util.List<String> itemSku;
        private String listName;

        public EditListRequest() { };


        public java.util.List<String> getItemSku() {
            return itemSku;
        }

        public void setItemSku(java.util.List<String> itemSku) {
            this.itemSku = itemSku;
        }

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }
    }

    @PostMapping(value="/saveEdit")
    public ResponseEntity<UserController.ResponseMessage> saveEditList(@RequestBody EditListRequest request)
    {
        try {
            User user = userService.getCurrentUser();
            List list = listService.findListByName(request.getListName());
            java.util.List<ItemList> items = itemListService.findByUsernameAndListId(user.getId(), list.getId());

            itemListDAO.deleteAll(items);

            ItemList itemList;
            Item itemToAdd;

            for(String itemSku : request.getItemSku()) {

                itemToAdd = itemService.findItemBySku(itemSku);
                itemList = new ItemList(itemToAdd, user,list);
                itemListService.saveItemList(itemList);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(new UserController.ResponseMessage("Something went wrong"), HttpStatus.valueOf(404));
        }

        return new ResponseEntity<>(new UserController.ResponseMessage("List was edited"), HttpStatus.valueOf(200));
    }



    @GetMapping(value= "/fetch-right-list")
    public ResponseEntity<java.util.List<Item>>
    fetchRightList(@RequestParam(value="listName") String name)
    {
        User user = userService.getCurrentUser();
        List list = listService.findListByName(name);

        java.util.List<ItemList> results = itemListService.findByUsernameAndListId(user.getId(), list.getId());

        java.util.List<Item> items = new ArrayList<>();

        for(ItemList record : results) {
            items.add( record.getItem() );
        }

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PostMapping(value="/price-items")
    public String priceItems(@RequestParam(value="listName") String name, Model theModel) {

        User user = userService.getCurrentUser();
        List list = listService.findListByName(name);

        java.util.List<ItemList> results = itemListService.findByUsernameAndListId(user.getId(), list.getId());

        java.util.List<String> apiRequests = results.stream().map(itemList-> "https://api2.prices.tf/prices/" + itemList.getItem().getSku())
                .collect(Collectors.toList());

        String[] apiRequestsArray = apiRequests.toArray(new String[apiRequests.size()]);

        WebClient client = WebClient.builder().baseUrl("https://api2.prices.tf").build();

        // Send a POST request to /auth/access to obtain the access token
        String accessToken = client.post()
                .uri("/auth/access")
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block()
                .getAccessToken();

        // Use the access token to authenticate subsequent requests
        java.util.List<ItemRequest> itemRequests = Flux
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
                .collect(Collectors.toList())
                .block();

        java.util.List<ItemRequestResult > resultList = new ArrayList<>();
        Item item;
        Double backpackPrice;

        for(ItemRequest req : itemRequests) {
            ItemList itemSearch = results.stream()
                    .filter(record -> record.getItem().getSku().equals(req.getSku()))
                    .findFirst()
                    .orElse(null);
            item = itemSearch.getItem();

            backpackPrice =  Double.valueOf(req.sellHalfScrap) / 16;
            resultList.add( new ItemRequestResult(item.getSku(), item.getName(), "0.3", backpackPrice.toString(), "2"));
        }

        resultList.stream().forEach(x-> System.out.println(x.getSku() + " " + x.getBackpackPrice()));

        theModel.addAttribute("resultItems", resultList);

        return "lists/create-profit-list";
    }

    public static class ItemRequestResult {
        private String sku;
        private String name;
        private String marketplacePrice;

        private String backpackPrice;

        private String calculatedProfit;

        public ItemRequestResult(String sku, String name, String marketplacePrice, String backpackPrice, String calculatedProfit) {
            this.sku = sku;
            this.name = name;
            this.marketplacePrice = marketplacePrice;
            this.backpackPrice = backpackPrice;
            this.calculatedProfit = calculatedProfit;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMarketplacePrice() {
            return marketplacePrice;
        }

        public void setMarketplacePrice(String marketplacePrice) {
            this.marketplacePrice = marketplacePrice;
        }

        public String getBackpackPrice() {
            return backpackPrice;
        }

        public void setBackpackPrice(String backpackPrice) {
            this.backpackPrice = backpackPrice;
        }

        public String getCalculatedProfit() {
            return calculatedProfit;
        }

        public void setCalculatedProfit(String calculatedProfit) {
            this.calculatedProfit = calculatedProfit;
        }
    }



    public static class ItemRequest {
        private String sku;
        private int buyHalfScrap;
        private int buyKeys;
        private Integer buyKeyHalfScrap;
        private int sellHalfScrap;
        private int sellKeys;
        private Integer sellKeyHalfScrap;
        private String createdAt;
        private String updatedAt;

        public ItemRequest() {
        }

        public ItemRequest(String sku, int buyHalfScrap, int buyKeys, Integer buyKeyHalfScrap, int sellHalfScrap, int sellKeys, Integer sellKeyHalfScrap, String createdAt, String updatedAt) {
            this.sku = sku;
            this.buyHalfScrap = buyHalfScrap;
            this.buyKeys = buyKeys;
            this.buyKeyHalfScrap = buyKeyHalfScrap;
            this.sellHalfScrap = sellHalfScrap;
            this.sellKeys = sellKeys;
            this.sellKeyHalfScrap = sellKeyHalfScrap;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public int getBuyHalfScrap() {
            return buyHalfScrap;
        }

        public void setBuyHalfScrap(int buyHalfScrap) {
            this.buyHalfScrap = buyHalfScrap;
        }

        public int getBuyKeys() {
            return buyKeys;
        }

        public void setBuyKeys(int buyKeys) {
            this.buyKeys = buyKeys;
        }

        public Integer getBuyKeyHalfScrap() {
            return buyKeyHalfScrap;
        }

        public void setBuyKeyHalfScrap(Integer buyKeyHalfScrap) {
            this.buyKeyHalfScrap = buyKeyHalfScrap;
        }

        public int getSellHalfScrap() {
            return sellHalfScrap;
        }

        public void setSellHalfScrap(int sellHalfScrap) {
            this.sellHalfScrap = sellHalfScrap;
        }

        public int getSellKeys() {
            return sellKeys;
        }

        public void setSellKeys(int sellKeys) {
            this.sellKeys = sellKeys;
        }

        public Integer getSellKeyHalfScrap() {
            return sellKeyHalfScrap;
        }

        public void setSellKeyHalfScrap(Integer sellKeyHalfScrap) {
            this.sellKeyHalfScrap = sellKeyHalfScrap;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        @Override
        public String toString() {
            return "ItemRequest{" +
                    "sku='" + sku + '\'' +
                    ", buyHalfScrap=" + buyHalfScrap +
                    ", buyKeys=" + buyKeys +
                    ", buyKeyHalfScrap=" + buyKeyHalfScrap +
                    ", sellHalfScrap=" + sellHalfScrap +
                    ", sellKeys=" + sellKeys +
                    ", sellKeyHalfScrap=" + sellKeyHalfScrap +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    '}';
        }
    }

    public static class AccessTokenResponse {
        private String accessToken;

        AccessTokenResponse() { };

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

}