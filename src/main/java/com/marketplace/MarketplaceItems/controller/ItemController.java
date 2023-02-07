package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    public ItemController(ItemService theItemService) {
        itemService = theItemService;
    }

    @GetMapping("/list")
    public String listItems(Model theModel, HttpSession session, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String craftable, @RequestParam(defaultValue = "") List<String> classes,
    @RequestParam(defaultValue = "") List<String> qualities, @RequestParam(defaultValue = "") List<String> types) {

        int pageSize = 1;
        Page<Item> items;
        Pageable pageable = PageRequest.of(page,pageSize);

        System.out.println(search);

        if(search != null && !search.equals("")) {
            System.out.println("Search");
            items = itemService.findAll(pageable, search);
        } else {
            items = itemService.findAll(pageable, craftable, classes, qualities, types);
            theModel.addAttribute("selectedCraftable", craftable);
            System.out.println("Selected: " + craftable);
            theModel.addAttribute("selectedClasses", classes);
            theModel.addAttribute("selectedQualities", qualities);
            theModel.addAttribute("selectedTypes", types);
        }

        // create list of values to checkboxes and attach to the model
        List<String> classesList = Arrays.asList("Multi-class", "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        List<String> qualityList = Arrays.asList("Genuine", "Vintage", "Unique", "Strange", "Haunted");
        List<String> typeList = Arrays.asList("Cosmetics", "Currencies", "Tools", "Paints", "Action", "Weapons", "Strange Parts", "Botkillers", "Festive Weapons", "Halloween");

        theModel.addAttribute("craftableOptions", Arrays.asList("Any", "Yes", "No"));
        theModel.addAttribute("classesList", classesList);
        theModel.addAttribute("qualityList", qualityList);
        theModel.addAttribute("typeList", typeList);

        String message = (String) session.getAttribute("message");

        if (message != null) {
            theModel.addAttribute("message", message);
            session.removeAttribute("message");
        }

        int totalPages = items.getTotalPages();
        theModel.addAttribute("totalPages", totalPages);

        List<Integer> pageArray = IntStream.range(0, totalPages).boxed().collect(Collectors.toList());
        theModel.addAttribute("pageArray", pageArray);

        // add to the spring model
        theModel.addAttribute("items", items);
        theModel.addAttribute("currentPage", page);

        Item item = new Item();
        theModel.addAttribute("item",item);

        return "items/list-items";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute("item") Item item) {
        itemService.saveItem(item);
        return "redirect:/items/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(value = "itemSku") String itemSku, HttpSession session) {

        System.out.println(itemSku);

        try {
            itemService.deleteBySku(itemSku);
        } catch (Exception e) {
            session.setAttribute("message", "Error occured while removing item");
            return "redirect:/items/list";
        }

        session.setAttribute("message", "Item was removed");
        return "redirect:/items/list";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("item") Item item) {
        itemService.updateItem(item);
        return "redirect:/items/list";
    }


}
