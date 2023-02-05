package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    public ItemController(ItemService theItemService) {
        itemService = theItemService;
    }

    @GetMapping("/list")
    public String listItems(Model theModel, HttpSession session) {

        String message = (String) session.getAttribute("message");

        System.out.println("Message: " + message);
        if (message != null) {
            theModel.addAttribute("message", message);
            session.removeAttribute("message");
        }

        // get employees from db
        List<Item> items = itemService.findAll();

        // add to the spring model
        theModel.addAttribute("items", items);

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
