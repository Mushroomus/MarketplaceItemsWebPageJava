package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.ItemService;
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

        if(page < 0)
            page = 0;

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
        String messageType = (String) session.getAttribute("messageType");

        if (message != null) {
            theModel.addAttribute("message", message);
            theModel.addAttribute("messageType", messageType);
            session.removeAttribute("message");
            session.removeAttribute("messageType");
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

    private String redirect(String search, int page, int totalPages, String craftable, List<String> classes, List<String> qualities, List<String> types) {

        // that if is made to check we will not return page of out range when we delete any Item, but still problem persist with update
        //  a lot of data need to be passed to check if edited item will disappear if filter was used
        if( totalPages != -1 && page >= totalPages - 1 && page != 0)
            page--;

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

    public void setMessageAttributes(HttpSession session, String message, String typeMessage) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", typeMessage);
    }

    private String validationItem(Item item, HttpSession session, boolean edit) {

        String sku = item.getSku();
        String name = item.getName();

        if(sku == null || sku.equals("")) {
            setMessageAttributes(session, "Sku is empty", "danger");
            return "invalid";
        }

        if(edit == false) {
            Item existingItem = itemService.findItemBySku(sku);
            if (existingItem != null) {
                setMessageAttributes(session, "Item already exists", "danger");
                return "invalid";
            }
        }

        if(name == null || name.equals("")) {
            setMessageAttributes(session, "Name is empty", "danger");
            return "invalid";
        }

        return "valid";
    }

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

    @PostMapping("/delete")
    public String delete(@RequestParam(value = "itemSku") String itemSku, HttpSession session, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int totalPages, @RequestParam(defaultValue = "") String craftableForm, @RequestParam(defaultValue = "") List<String> classes,
                         @RequestParam(defaultValue = "") List<String> qualities, @RequestParam(defaultValue = "") List<String> types) {
        try {
            itemService.deleteBySku(itemSku);
            setMessageAttributes(session, "Item was deleted", "success");
        } catch (Exception e) {
            setMessageAttributes(session, "Error occured while removing an item", "danger");
        }
        return redirect(search, page, totalPages, craftableForm, classes, qualities, types);
    }

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

    @GetMapping("/create-list")
    public String createTable() {

        return "items/create-list-items";
    }

    @GetMapping("/fetch-list")
    public ResponseEntity<PagedModel<Item>> fetchList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "5") int size,
                                                      @RequestParam(defaultValue="") String search,
                                                      @RequestParam(defaultValue = "") String craftable,
                                                      @RequestParam(defaultValue = "") List<String> classes,
                                                      @RequestParam(defaultValue = "") List<String> qualities,
                                                      @RequestParam(defaultValue = "") List<String> types)
    {
        Page<Item> items;
        Pageable pageable = PageRequest.of(page, 5);

        items = itemService.findAllFilters(pageable, search, craftable, classes, qualities, types);

        PagedModel<Item> pagedModel = PagedModel.of(items.getContent(), new PagedModel.PageMetadata(items.getSize(), items.getNumber(), items.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }


}
