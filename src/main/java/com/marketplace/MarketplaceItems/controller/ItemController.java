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
    public String listItems(Model theModel, HttpSession session, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") List<String> classes) {

        System.out.println(classes);

        List<String> classesList = Arrays.asList("Soldier", "Pyro");
        // Add the list to the model
        theModel.addAttribute("classesList", classesList);

        theModel.addAttribute("selectedClasses", classes);



        String message = (String) session.getAttribute("message");

        System.out.println("Message: " + message);
        if (message != null) {
            theModel.addAttribute("message", message);
            session.removeAttribute("message");
        }

        int pageSize = 1;
        Pageable pageable = PageRequest.of(page,pageSize);

        Page<Item> items = itemService.findAll(pageable, classes);


        int totalPages = items.getTotalPages();
        theModel.addAttribute("totalPages", totalPages);

        List<Integer> pageArray = IntStream.range(0, totalPages).boxed().collect(Collectors.toList());
        theModel.addAttribute("pageArray", pageArray);

        // get employees from db
        //List<Item> items = itemService.findAll();

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
