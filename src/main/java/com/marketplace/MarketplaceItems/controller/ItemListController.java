package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.model.*;
import com.marketplace.MarketplaceItems.service.ItemListService;
import org.springframework.hateoas.PagedModel;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lists")
public class ItemListController {
    private final ItemListService itemListService;

    public ItemListController(ItemListService theItemListService) {
        itemListService = theItemListService;
    }

    @GetMapping("/show")
    public String showLists(Model theModel) {
        Map<String, Object> resultMap = itemListService.getListInfoAndButtonStatus();
        theModel.addAttribute("listInfo", resultMap.get("listInfo"));

        if(resultMap.get("redirect") != null) {
            return "redirect:/lists/create-list";
        } else {
            theModel.addAttribute("disableCreateButton", resultMap.get("disableCreateButton"));
        }

        String deleteAlert = (String) theModel.getAttribute("deleteAlert");
        if(deleteAlert != null && !deleteAlert.equals(""))
            theModel.addAttribute("deleteAlert", deleteAlert);

        return "lists/show-list-items";
    }

    @GetMapping("/delete")
    public String deleteList(@RequestParam(value = "listName") String name, RedirectAttributes redirectAttributes) {
        try {
            itemListService.deleteItemList(name);
            redirectAttributes.addFlashAttribute("deleteAlert", "List deleted");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("deleteAlert", "Something went wrong");
        }
        return "redirect:/lists/show";
    }

    @GetMapping("/create-list")
    public String createTable(Model theModel) {
        Map<String, Object> model = itemListService.getCreateListTableData();
        theModel.addAllAttributes(model);
        return "lists/create-list-items";
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ResponseMessage> createList(@RequestBody CreateListRequest request) {
        return itemListService.createList(request);
    }

    @GetMapping
    public ResponseEntity<PagedModel<Item>>
    getItemList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "5") int size,
                                                      @RequestParam(defaultValue="") String search,
                                                      @RequestParam(defaultValue = "") String craftable,
                                                      @RequestParam(defaultValue = "") List<String> classes,
                                                      @RequestParam(defaultValue = "") List<String> qualities,
                                                      @RequestParam(defaultValue = "") List<String> types) {
        return itemListService.getItemList(page, size, search, craftable, classes, qualities, types);
    }

    @GetMapping(value= "/edit")
    public String editList(@RequestParam(value="listName") String name, Model theModel)
    {
        theModel.addAttribute("listName", name);
        List<String> classesList = Arrays.asList("Multi-class", "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        List<String> qualityList = Arrays.asList("Genuine", "Vintage", "Unique", "Strange", "Haunted");
        List<String> typeList = Arrays.asList("Cosmetics", "Currencies", "Tools", "Paints", "Action", "Weapons", "Strange Parts", "Botkillers", "Festive Weapons", "Halloween");

        theModel.addAttribute("craftableOptions", Arrays.asList("Any", "Yes", "No"));
        theModel.addAttribute("classesList", classesList);
        theModel.addAttribute("qualityList", qualityList);
        theModel.addAttribute("typeList", typeList);

        return "lists/edit-list-items";
    }

    @PostMapping(value="/saveEdit")
    @Transactional
    public ResponseEntity<ResponseMessage> saveEditList(@RequestBody EditListRequest request) {
        return itemListService.saveEditedList(request);
    }

    @GetMapping(value= "/fetch-right-list")
    public ResponseEntity<List<Item>> fetchRightList(@RequestParam(value="listName") String name) {
        return itemListService.fetchRightList(name);
    }

    @PostMapping(value="/price-items")
    public String priceItems(@RequestParam(value="listName") String name, @RequestParam(value="marketplaceKeyPrice") String marketplaceKeyPrice, Model theModel) {
        List<ItemRequestResult> resultList = itemListService.priceItems(name, marketplaceKeyPrice);
        theModel.addAttribute("resultItems", resultList);
        return "lists/create-profit-list";
    }
}
