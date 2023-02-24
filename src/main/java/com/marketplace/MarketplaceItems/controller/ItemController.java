package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.ItemListService;
import com.marketplace.MarketplaceItems.service.ItemService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    private ItemListService itemListService;

    public ItemController(@Qualifier("itemServiceImpl") ItemService theItemService, @Qualifier("itemListServiceImpl") ItemListService theItemListService) {
        itemService = theItemService;
        itemListService = theItemListService;
    }


    @GetMapping("/list")
    public String listItems(Model theModel) {

        List<String> classesList = Arrays.asList("Multi-class", "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        List<String> qualityList = Arrays.asList("Genuine", "Vintage", "Unique", "Strange", "Haunted");
        List<String> typeList = Arrays.asList("Cosmetics", "Currencies", "Tools", "Paints", "Action", "Weapons", "Strange Parts", "Botkillers", "Festive Weapons", "Halloween");

        theModel.addAttribute("craftableOptions", Arrays.asList("Any", "Yes", "No"));
        theModel.addAttribute("classesList", classesList);
        theModel.addAttribute("qualityList", qualityList);
        theModel.addAttribute("typeList", typeList);

        return "items/list-items";
    };


    @GetMapping("/list-refresh")
    public ResponseEntity<PagedModel<Item>> refreshList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "1") int size,
                                                        @RequestParam(value = "search", required = false) String search,
                                                        @RequestParam(defaultValue = "", required = false) String craftable,
                                                        @RequestParam(defaultValue = "", required = false) List<String> classes,
                                                        @RequestParam(defaultValue = "", required = false) List<String> qualities,
                                                        @RequestParam(defaultValue = "", required = false) List<String> types)

    {
        Page<Item> items;
        Pageable pageable = PageRequest.of(page, size);


        items = itemService.findAllFilters(pageable, search, craftable, classes, qualities, types);

        PagedModel<Item> pagedModel = PagedModel.of(items.getContent(), new PagedModel.PageMetadata(items.getSize(), items.getNumber(), items.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }


    /*
    @PostMapping("/add")
    public String addItem(@ModelAttribute("item") Item item, HttpSession session, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String craftableForm, @RequestParam(defaultValue = "") List<String> classes,
                          @RequestParam(defaultValue = "") List<String> qualities, @RequestParam(defaultValue = "") List<String> types) {

        if(validationItem(item,session,false).equals("valid")) {
            try{
                itemService.saveItem(item);
                setMessageAttributes(session, "Item was added", "success");
            } catch(Exception e) {
                setMessageAttributes(session, "Error occured while adding an item", "danger");
            }
        }

        return redirect(search, page, -1, craftableForm, classes, qualities, types);
    }
     */

    @GetMapping("/delete")
    @Transactional
    public ResponseEntity<UserController.ResponseMessage> delete(@RequestParam(value = "sku") String itemSku) {
        try {
            itemListService.deleteAllByItemSku(itemSku);
            itemService.deleteBySku(itemSku);
            return new ResponseEntity<>(new UserController.ResponseMessage("Item was deleted"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new UserController.ResponseMessage("Failed to delete Item"), HttpStatus.valueOf(400));
        }
    }

    /*
    @PostMapping("/update")
    public String update(@ModelAttribute("item") Item item, HttpSession session, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String craftableForm, @RequestParam(defaultValue = "") List<String> classes,
                         @RequestParam(defaultValue = "") List<String> qualities, @RequestParam(defaultValue = "") List<String> types) {

        if(validationItem(item,session,true).equals("valid")) {
            try {
                itemService.updateItem(item);
                setMessageAttributes(session, "Item was updated", "success");
            } catch (Exception e) {
                setMessageAttributes(session, "Error occured while updating an item", "danger");
            }
        }
        return redirect(search, page, -1, craftableForm, classes, qualities, types);
    }
     */
}
