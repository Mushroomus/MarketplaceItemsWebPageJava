package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    public ItemController(ItemService theItemService) {
        itemService = theItemService;
    }

    @GetMapping("/list")
    public String listItems(Model theModel) {

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
        System.out.println(item);
        itemService.saveItem(item);
        return "redirect:/items/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("itemSku") String itemSku) {
        itemService.deleteBySku(itemSku);
        return "redirect:/items/list";
    }


}
