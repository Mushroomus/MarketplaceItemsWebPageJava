package com.marketplace.MarketplaceItems.controller;


import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService theUserService) {
        userService = theUserService;
    }

    @GetMapping("/list")
    public String listItems(Model theModel, HttpSession session, @RequestParam(defaultValue = "0") int page) {
        return "user/list-users";
    }

    public void setMessageAttributes(HttpSession session, String message, String typeMessage) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", typeMessage);
    }

    private String validateUser(User user, String changePassword) {

        String username = user.getUsername();

        if(username == null || username.equals(""))
            return "Username is empty";


        User existingUser = userService.findByUsername(username);
        if (existingUser != null)
            return "Username already exists";

        String password;

        if(changePassword != null && !changePassword.equals(""))
            password = changePassword;
        else
            password = user.getPassword();

        String pattern = "^(?=.*[0-9])(?=.*[!@#$%^&+=])(?=\\S+$).{6,}$";

        if (!password.matches(pattern))
            return "Password should be at least 6 characters long and contain at least one number and one special character";

        return "valid";
    }

    public class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> addItem(@RequestBody User user) {

        String resultValidation = validateUser(user, null);

        if (!resultValidation.equals("valid"))
            return new ResponseEntity<>(new ResponseMessage(resultValidation), HttpStatus.BAD_REQUEST);

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
        user.setDate(LocalDateTime.now());

        try {
            userService.saveUser(user);
            return new ResponseEntity<>(new ResponseMessage("User was added"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Error occured while adding an user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public class PagedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;

        public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.last = last;
        }

        public PagedResponse(Page<T> page) {
            this.content = page.getContent();
            this.page = page.getNumber();
            this.size = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.last = page.isLast();
        }

        // getters and setters for each member variable
    }


    @GetMapping("/list-refresh")
    public ResponseEntity<PagedModel<User>> refreshList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "1") int size,
                                                        @RequestParam(value = "search", required = false) String search,
                                                        @RequestParam(value = "role", required = false) String role,
                                                        @RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        Page<User> users;
        Pageable pageable = PageRequest.of(page, size);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        LocalDateTime start = null;
        if (startDate != null && !startDate.equals("null")) {
            long timestamp = Long.parseLong(startDate);
            Date date = new Date(timestamp);
            String dateStartString = formatter.format(date);
            start = LocalDateTime.parse(dateStartString, formatterDate);
        }

        LocalDateTime end = null;
        if (endDate != null && !endDate.equals("null")) {
            long timestamp = Long.parseLong(endDate);
            Date date = new Date(timestamp);
            String dateEndString = formatter.format(date);
            end = LocalDateTime.parse(dateEndString, formatterDate);
        }


        users = userService.findAll(pageable, search, role, start, end);

        PagedModel<User> pagedModel = PagedModel.of(users.getContent(), new PagedModel.PageMetadata(users.getSize(), users.getNumber(), users.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }



    @GetMapping("/delete")
    public ResponseEntity<ResponseMessage> delete(@RequestParam int id) {
        try {
            userService.deleteUserById(id);
            return new ResponseEntity<>(new ResponseMessage("User was deleted"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Failed to delete user"), HttpStatus.OK);
        }
    }

    public static class UpdateUserRequest {
        private User user;
        private String changePassword;

        public UpdateUserRequest() { }

        public UpdateUserRequest(User user, String changePassword) {
            this.user = user;
            this.changePassword = changePassword;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getChangePassword() {
            return changePassword;
        }

        public void setChangePassword(String changePassword) {
            this.changePassword = changePassword;
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody UpdateUserRequest request) {

        System.out.println(request);
        User user = request.getUser();
        String changePassword = request.getChangePassword();

        System.out.println(user);
        System.out.println(changePassword);

        String resultValidation = validateUser(user, changePassword);

        if (!resultValidation.equals("valid")) {
            return new ResponseEntity<>(resultValidation, HttpStatus.BAD_REQUEST);
        }

        user.setDate(LocalDateTime. now());

        if(changePassword != null && !changePassword.equals(""))
            user.setPassword( BCrypt.hashpw(changePassword, BCrypt.gensalt(10)) );

        try {
            userService.updateUser(user);
            return new ResponseEntity<>("User was updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while updating the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    @PostMapping("/update")
    public String update(@ModelAttribute("user") User user, @ModelAttribute("changePassword") String changePassword, HttpSession session, @RequestParam(defaultValue = "0") int page) {

        String resultValidation = validateUser(user,session,page, changePassword);

        if(!resultValidation.equals("valid"))
            return resultValidation;

        user.setDate(LocalDateTime. now());

        if(changePassword != null && !changePassword.equals(""))
            user.setPassword( BCrypt.hashpw(changePassword, BCrypt.gensalt(10)) );

        System.out.println("After if " + user.getPassword());

        try {
            userService.updateUser(user);
            setMessageAttributes(session, "User was updated", "success");
        } catch (Exception e) {
            setMessageAttributes(session, "Error occured while updating an user", "danger");
        }
        return "redirect:list?page=" + page;
    }
     */

}
