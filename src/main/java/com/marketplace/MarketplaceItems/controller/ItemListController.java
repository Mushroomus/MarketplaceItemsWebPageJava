package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.dao.ItemListDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.ItemList;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.entity.List;
import com.marketplace.MarketplaceItems.service.ItemListService;
import com.marketplace.MarketplaceItems.service.ItemService;
import com.marketplace.MarketplaceItems.service.UserService;
import com.marketplace.MarketplaceItems.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping("/lists")
public class ItemListController {

    @Autowired
    private PagedResourcesAssembler<Item> assembler;

    private ItemService itemService;
    private UserService userService;

    private ListService listService;

    private ItemListService itemListService;
    private final ItemListDAO itemListDAO;

    public ItemListController(ItemService theItemService, UserService theUserService, ListService theListService, ItemListService theItemListService,
                              ItemListDAO itemListDAO) {

        itemService = theItemService;
        userService = theUserService;
        listService = theListService;
        itemListService = theItemListService;
        this.itemListDAO = itemListDAO;
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


    @GetMapping("/show")
    public String showLists(Model theModel) {
        User user = userService.getCurrentUser();
        java.util.List<User.ListInfoModel> listInfo = user.getListNamesWithItemCount();

        theModel.addAttribute("listInfo", listInfo);
        return "lists/show-list-items";
    }

    @GetMapping("/delete")
    public String deleteList(@RequestParam(value = "listName") String name) {

        List foundList = listService.findListByName(name);
        System.out.println(foundList.getName());

        if ( foundList != null ) {

            java.util.List<ItemList> itemsList = itemListService.findByListId(foundList.getId());

            itemListDAO.deleteAll(itemsList);
            listService.deleteList(foundList);
        }

        return "redirect:/lists/show";
    }

    @GetMapping("/create-list")
    public String createTable() {

        return "lists/create-list-items";
    }

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<UserController.ResponseMessage>
    createList(@RequestBody CreateListRequest request) {

        String username = request.getUsername();
        java.util.List<String> itemSku = request.getItemSku();
        String listName = request.getListName();

        User user = userService.findByUsername(username);

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

            ItemList itemList = new ItemList(item,user,list);
            itemListService.saveItemList(itemList);
        }

        return new ResponseEntity<>(new UserController.ResponseMessage("List was created"), HttpStatus.CREATED);
    }



    @GetMapping(value= "/fetch-list")
    public ResponseEntity<PagedModel<Item>>
    fetchList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "5") int size,
                                                      @RequestParam(defaultValue="") String search,
                                                      @RequestParam(defaultValue = "") String craftable,
                                                      @RequestParam(defaultValue = "") java.util.List<String> classes,
                                                      @RequestParam(defaultValue = "") java.util.List<String> qualities,
                                                      @RequestParam(defaultValue = "") java.util.List<String> types)
    {

        Page<Item> items;
        Pageable pageable = PageRequest.of(page, 5);

        items = itemService.findAllFilters(pageable, search, craftable, classes, qualities, types);

        PagedModel<Item> pagedModel = PagedModel.of(items.getContent(), new PagedModel.PageMetadata(items.getSize(), items.getNumber(), items.getTotalElements()));

        System.out.println("Paged Model " + pagedModel);

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);

    }

    @GetMapping(value= "/edit")
    public String editList(@RequestParam(value="listName") String name, Model theModel)
    {
        theModel.addAttribute("listName", name);
        return "lists/edit-list-items";
    }

    public static class EditListRequest {

        private java.util.List<String> itemSku;
        private String listName;

        public EditListRequest() { };


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

    @PostMapping(value="/saveEdit")
    public ResponseEntity<UserController.ResponseMessage> saveEditList(@RequestBody EditListRequest request)
    {
        try {
            User user = userService.getCurrentUser();
            List list = listService.findListByName(request.getListName());
            java.util.List<ItemList> items = itemListService.findByUsernameAndListId(user.getId(), list.getId());

            itemListDAO.deleteAll(items);

            ItemList itemList;
            Item itemToAdd;

            for(String itemSku : request.getItemSku()) {

                itemToAdd = itemService.findItemBySku(itemSku);
                itemList = new ItemList(itemToAdd, user,list);
                itemListService.saveItemList(itemList);
            }
        } catch(Exception e) {
            return new ResponseEntity<>(new UserController.ResponseMessage("Something went wrong"), HttpStatus.valueOf(404));
        }

        return new ResponseEntity<>(new UserController.ResponseMessage("List was edited"), HttpStatus.valueOf(200));
    }



    @GetMapping(value= "/fetch-right-list")
    public ResponseEntity<java.util.List<Item>>
    fetchRightList(@RequestParam(value="listName") String name)
    {
        User user = userService.getCurrentUser();
        List list = listService.findListByName(name);

        java.util.List<ItemList> results = itemListService.findByUsernameAndListId(user.getId(), list.getId());

        java.util.List<Item> items = new ArrayList<>();

        for(ItemList record : results) {
            items.add( record.getItem() );
        }

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

}
