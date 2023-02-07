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

        if(search != null && !search.equals("")) {
            items = itemService.findAll(pageable, search);
            theModel.addAttribute("searchedData", search);
        } else {
            items = itemService.findAll(pageable, craftable, classes, qualities, types);

            theModel.addAttribute("selectedCraftable", craftable);
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

    private String redirect(String search, int page, String craftable, List<String> classes, List<String> qualities, List<String> types) {
        if(search != null && !search.equals("")) {
            return "redirect:/items/list?page=" + page + "&search=" + search;
        } else {

            StringBuilder urlBuilder = new StringBuilder("/items/list?page=" + page);

            String classesString = classes.toString().replace("[", "").replace("]", "").replace(", ", ",");
            String qualitiesString = qualities.toString().replace("[", "").replace("]", "").replace(", ", ",");
            String typesString = types.toString().replace("[", "").replace("]", "").replace(", ", ",");

            if (!craftable.isEmpty())
                urlBuilder.append("&craftable=" + craftable);

            if (!classesString .isEmpty())
                urlBuilder.append("&classes=" + String.join(",", classesString));

            if (!qualitiesString.isEmpty())
                urlBuilder.append("&qualities=" + String.join(",", qualitiesString));

            if (!typesString.isEmpty())
                urlBuilder.append("&types=" + String.join(",", typesString));

            return "redirect:" + urlBuilder.toString();


        }
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute("item") Item item, HttpSession session, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String craftableForm, @RequestParam(defaultValue = "") List<String> classes,
                          @RequestParam(defaultValue = "") List<String> qualities, @RequestParam(defaultValue = "") List<String> types) {
        try{
            itemService.saveItem(item);
            session.setAttribute("message", "Item was added");
        } catch(Exception e) {
            session.setAttribute("message", "Error occured while adding an item");
        }

        return redirect(search, page, craftableForm, classes, qualities, types);
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(value = "itemSku") String itemSku, HttpSession session, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String craftableForm, @RequestParam(defaultValue = "") List<String> classes,
                         @RequestParam(defaultValue = "") List<String> qualities, @RequestParam(defaultValue = "") List<String> types) {
        try {
            itemService.deleteBySku(itemSku);
            session.setAttribute("message", "Item was deleted");
        } catch (Exception e) {
            session.setAttribute("message", "Error occured while removing an item");
        }
        return redirect(search, page, craftableForm, classes, qualities, types);
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("item") Item item, HttpSession session, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String craftableForm, @RequestParam(defaultValue = "") List<String> classes,
                         @RequestParam(defaultValue = "") List<String> qualities, @RequestParam(defaultValue = "") List<String> types) {
        try {
            itemService.updateItem(item);
            session.setAttribute("message", "Item was updated");
        } catch (Exception e) {
            session.setAttribute("message", "Error occured while updating an item");
        }
        return redirect(search, page, craftableForm, classes, qualities, types);
    }


}
