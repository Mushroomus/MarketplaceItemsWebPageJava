package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.ItemService;
import com.marketplace.MarketplaceItems.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService theUserService) {
        userService = theUserService;
    }

    @GetMapping("/list")
    public String listItems(Model theModel, HttpSession session, @RequestParam(defaultValue = "0") int page) {

        int pageSize = 10;
        Page<User> users;
        Pageable pageable = PageRequest.of(page,pageSize);

        String message = (String) session.getAttribute("message");
        String messageType = (String) session.getAttribute("messageType");

        if (message != null) {
            theModel.addAttribute("message", message);
            theModel.addAttribute("messageType", messageType);
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

        users = userService.findAll(pageable);

        int totalPages = users.getTotalPages();
        theModel.addAttribute("totalPages", totalPages);

        List<Integer> pageArray = IntStream.range(0, totalPages).boxed().collect(Collectors.toList());
        theModel.addAttribute("pageArray", pageArray);

        // add to the spring model
        theModel.addAttribute("users", users);
        theModel.addAttribute("currentPage", page);

        User user = new User();
        theModel.addAttribute("user", user);

        return "user/list-users";
    }

    public void setMessageAttributes(HttpSession session, String message, String typeMessage) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", typeMessage);
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute("user") User user, HttpSession session, @RequestParam(defaultValue = "0") int page) {

        user.setPassword( BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)) );
        user.setDate(LocalDateTime. now());

        try{
            userService.saveUser(user);
            setMessageAttributes(session, "User was added", "success");
        } catch(Exception e) {
            setMessageAttributes(session, "Error occured while adding an user", "danger");
        }
        return "redirect:list?page=" + page;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(value = "userId") int id, HttpSession session, @RequestParam(defaultValue = "0") int page) {
        try {
            userService.deleteUserById(id);
            setMessageAttributes(session, "User was deleted", "success");
        } catch (Exception e) {
            setMessageAttributes(session, "Error occured while removing an user", "danger");
        }
        return "redirect:list?page=" + page;
    }


}
