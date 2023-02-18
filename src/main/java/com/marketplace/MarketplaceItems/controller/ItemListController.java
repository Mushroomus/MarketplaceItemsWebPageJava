package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.ItemList;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.entity.List;
import com.marketplace.MarketplaceItems.service.ItemListService;
import com.marketplace.MarketplaceItems.service.ItemService;
import com.marketplace.MarketplaceItems.service.UserService;
import com.marketplace.MarketplaceItems.service.ListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/lists")
public class ItemListController {

    private ItemService itemService;
    private UserService userService;

    private ListService listService;

    private ItemListService itemListService;

    public ItemListController(ItemService theItemService, UserService theUserService, ListService theListService, ItemListService theItemListService) {

        itemService = theItemService;
        userService = theUserService;
        listService = theListService;
        itemListService = theItemListService;
    }


    public static class CreateListRequest {
        private String username;
        private java.util.List<String> itemSku;
        private String listName;

        public CreateListRequest() { };

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public java.util.List<String> getItemSku() {
            return itemSku;
        }

        public void setItemSku(java.util.List<String> itemSku) {
            this.itemSku = itemSku;
        }

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }
    }

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<UserController.ResponseMessage> createList(@RequestBody CreateListRequest request) {

        String username = request.getUsername();
        java.util.List<String> itemSku = request.getItemSku();
        String listName = request.getListName();

        User user = userService.findByUsername(username);

        System.out.println(user);

        if (user == null) {
            return new ResponseEntity<>(new UserController.ResponseMessage("User not found"), HttpStatus.BAD_REQUEST);
        }

        List list = new List();
        list.setName(listName);
        list.setDate(LocalDateTime.now());
        listService.saveList(list);


        for (String theItemSku : itemSku) {

            Item item = itemService.findItemBySku(theItemSku);

            if (item == null) {
                return new ResponseEntity<>(new UserController.ResponseMessage("Item not found"), HttpStatus.BAD_REQUEST);
            }

            ItemList itemList = new ItemList();
            itemList.setUser(user);
            itemList.setItem(item);
            itemList.setList(list);

            itemListService.saveItemList(itemList);
        }

        return new ResponseEntity<>(new UserController.ResponseMessage("List was created"), HttpStatus.CREATED);
    }
}
