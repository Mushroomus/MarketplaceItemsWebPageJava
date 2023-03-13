package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.ItemImage;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.ItemImageService;
import com.marketplace.MarketplaceItems.service.ItemListService;
import com.marketplace.MarketplaceItems.service.ItemService;
import com.marketplace.MarketplaceItems.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    private ItemListService itemListService;

    private MessageService messageService;

    private ItemImageService itemImageService;

    public ItemController(@Qualifier("itemServiceImpl") ItemService theItemService, @Qualifier("itemListServiceImpl") ItemListService theItemListService,
                          @Qualifier("messageServiceImpl") MessageService theMessageService, ItemImageService theItemImageService) {
        itemService = theItemService;
        itemListService = theItemListService;
        messageService = theMessageService;
        itemImageService = theItemImageService;
    }


    @GetMapping("/list-admin")
    public String listItemsAdmin(Model theModel) {

        List<String> classesList = Arrays.asList("Multi-class", "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        List<String> qualityList = Arrays.asList("Genuine", "Vintage", "Unique", "Strange", "Haunted");
        List<String> typeList = Arrays.asList("Cosmetics", "Currencies", "Tools", "Paints", "Action", "Weapons", "Strange Parts", "Botkillers", "Festive Weapons", "Halloween");

        theModel.addAttribute("craftableOptions", Arrays.asList("Any", "Yes", "No"));
        theModel.addAttribute("classesList", classesList);
        theModel.addAttribute("qualityList", qualityList);
        theModel.addAttribute("typeList", typeList);

        return "items/list-items-admin";
    }

    @GetMapping("/list-user")
    public String listItemsUser(Model theModel) {

        List<String> classesList = Arrays.asList("Multi-class", "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        List<String> qualityList = Arrays.asList("Genuine", "Vintage", "Unique", "Strange", "Haunted");
        List<String> typeList = Arrays.asList("Cosmetics", "Currencies", "Tools", "Paints", "Action", "Weapons", "Strange Parts", "Botkillers", "Festive Weapons", "Halloween");

        theModel.addAttribute("craftableOptions", Arrays.asList("Any", "Yes", "No"));
        theModel.addAttribute("classesList", classesList);
        theModel.addAttribute("qualityList", qualityList);
        theModel.addAttribute("typeList", typeList);

        return "items/list-items-user";
    }


    @GetMapping("/list-refresh")
    public ResponseEntity<PagedModel<Item>> refreshList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "1") int size,
                                                        @RequestParam(defaultValue = "", required = false) String search,
                                                        @RequestParam(defaultValue = "", required = false) String craftable,
                                                        @RequestParam(defaultValue = "", required = false) List<String> classes,
                                                        @RequestParam(defaultValue = "", required = false) List<String> qualities,
                                                        @RequestParam(defaultValue = "", required = false) List<String> types) {
        Page<Item> items;
        Pageable pageable = PageRequest.of(page, size);

        items = itemService.findAllFilters(pageable, search, craftable, classes, qualities, types);
        System.out.println(items);

        PagedModel<Item> pagedModel = PagedModel.of(items.getContent(), new PagedModel.PageMetadata(items.getSize(), items.getNumber(), items.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<UserController.ResponseMessage> addItem(@RequestBody Item item) {
        try {

            String skuPrefix = item.getSku().split(";")[0];
            String image_url = "";

            if (skuPrefix.matches("\\d+")) {
                Integer shortenSku = Integer.parseInt(item.getSku().split(";")[0]);
                image_url = itemImageService.findByDefindexReturnUrl(shortenSku);
            }

            item.setImage(image_url);
            
            itemService.saveItem(item);
            return new ResponseEntity<>(new UserController.ResponseMessage("Item was added"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new UserController.ResponseMessage("Error occured while adding an Item"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/delete")
    @Transactional
    public ResponseEntity<UserController.ResponseMessage> delete(@RequestParam(value = "sku") String itemSku) {
        try {
            itemListService.deleteAllByItemSku(itemSku);
            messageService.deleteAllByItemSku(itemSku);
            itemService.deleteBySku(itemSku);
            return new ResponseEntity<>(new UserController.ResponseMessage("Item was deleted"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new UserController.ResponseMessage("Failed to delete Item"), HttpStatus.valueOf(400));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody Item item) {
        try {
            itemService.updateItem(item);
            return new ResponseEntity<>("Item was updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while updating the item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updatePrice")
    public ResponseEntity<String> updatePrice(@RequestBody Map<String, Object> requestBody) {
        try {
            String sku = (String) requestBody.get("sku");
            Double mpPrice = Double.parseDouble( (String) requestBody.get("mpPrice"));

            Item item = itemService.findItemBySku(sku);
            item.setMarketplacePrice(mpPrice);
            itemService.saveItem(item);

            return new ResponseEntity<>("Price was updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
