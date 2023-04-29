package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import com.marketplace.MarketplaceItems.model.UpdateUserRequest;
import com.marketplace.MarketplaceItems.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(@Qualifier("userServiceImpl") UserService theUserService) {
        userService = theUserService;
    }

    @GetMapping("/list")
    public String showUserList() {
        return "user/list-users";
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping
    public ResponseEntity<PagedModel<User>> getUserList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "5") int size,
                                                        @RequestParam(value = "search", required = false) String search,
                                                        @RequestParam(value = "role", required = false) String role,
                                                        @RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        return userService.getUserList(page, size, search, role, startDate, endDate);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<ResponseMessage> deleteUser(@RequestParam int id) {
        return userService.deleteUser(id);
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }
}
