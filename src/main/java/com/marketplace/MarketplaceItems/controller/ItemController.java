package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import com.marketplace.MarketplaceItems.service.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    public ItemController(@Qualifier("itemServiceImpl") ItemService theItemService) {
        itemService = theItemService;
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


    @GetMapping
    public ResponseEntity<PagedModel<Item>> getItems(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "", required = false) String search,
                                                        @RequestParam(defaultValue = "", required = false) String craftable,
                                                        @RequestParam(defaultValue = "", required = false) List<String> classes,
                                                        @RequestParam(defaultValue = "", required = false) List<String> qualities,
                                                        @RequestParam(defaultValue = "", required = false) List<String> types) {
        return itemService.getItems(page, size, search, craftable, classes, qualities, types);
    }


    @PostMapping
    @Transactional
    public ResponseEntity<ResponseMessage> addItem(@RequestBody Item item) {
        return itemService.addItem(item);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<ResponseMessage> delete(@RequestParam(value = "sku") String itemSku) {
       return itemService.deleteItem(itemSku);
    }

    @PutMapping
    public ResponseEntity<ResponseMessage> updateItem(@RequestBody Item item) {
        return itemService.updateItem(item);
    }

    @PutMapping("/marketplace-price")
    public ResponseEntity<ResponseMessage> updateItemPrice(@RequestBody Map<String, Object> requestBody) {
        return itemService.updateItemPrice(requestBody);
    }
}
