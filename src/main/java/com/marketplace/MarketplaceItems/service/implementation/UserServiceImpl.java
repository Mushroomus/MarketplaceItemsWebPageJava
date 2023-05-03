package com.marketplace.MarketplaceItems.service.implementation;

import com.marketplace.MarketplaceItems.dao.UserDAO;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.exception.BadRequestException;
import com.marketplace.MarketplaceItems.exception.InternalServerErrorException;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import com.marketplace.MarketplaceItems.model.UpdateUserRequest;
import com.marketplace.MarketplaceItems.service.UserService;
import com.marketplace.MarketplaceItems.service.operation.ItemListUserOperations;
import com.marketplace.MarketplaceItems.service.operation.MessageUserOperations;
import com.marketplace.MarketplaceItems.service.operation.SaleUserOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class UserServiceImpl implements UserService, MessageUserOperations, ItemListUserOperations, SaleUserOperations {

    private UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO theUserDAO) {
        userDAO = theUserDAO;
    }

    private String validateUser(User user, String changePassword, boolean ifUpdate) {
        String username = user.getUsername();

        if(username == null || username.equals(""))
            return "Username is empty";

        if(!ifUpdate) {
            User existingUser =  userDAO.findByUsername(username);
            if (existingUser != null)
                return "Username already exists";
        }

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

    @Override
    public ResponseEntity<ResponseMessage> addUser(@RequestBody User user) {
        String resultValidation = validateUser(user, null, false);

        if (!resultValidation.equals("valid"))
            return new ResponseEntity<>(new ResponseMessage(resultValidation), HttpStatus.BAD_REQUEST);

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
        user.setDate(LocalDateTime.now());

        try {
            userDAO.save(user);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("User was added"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occured while adding an user");
        }
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || "null".equals(dateStr)) {
            return null;
        }
        long timestamp = Long.parseLong(dateStr);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
    }

    @Override
    public ResponseEntity<PagedModel<User>> getUserList(int page, int size, String search, String role, String startDate, String endDate) {
        Page<User> users;
        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime start = parseDate(startDate);
        LocalDateTime end = parseDate(endDate);

        users =  userDAO.findAll(search, role, start, end, getCurrentUser().getId(), pageable);
        PagedModel<User> pagedModel = PagedModel.of(users.getContent(), new PagedModel.PageMetadata(users.getSize(), users.getNumber(), users.getTotalElements()));

        return ResponseEntity.status(HttpStatus.OK).body(pagedModel);
    }

    @Override
    public ResponseEntity<ResponseMessage> deleteUser(@RequestParam int id) {
        /*
        itemListService.deleteAllByUserId(id);
        messageService.deleteAllByUserId(id);
        saleService.deleteAllByUserId(id);
        userService.deleteUserById(id);
         */
        try {
            userDAO.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("User was deleted"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete user");
        }
    }

    @Override
    public ResponseEntity<String> updateUser(UpdateUserRequest request) {

        User user = request.getUser();
        String changePassword = request.getChangePassword();

        String resultValidation = validateUser(user, changePassword, true);

        if (!resultValidation.equals("valid")) {
            throw new BadRequestException(resultValidation);
        }

        user.setDate(LocalDateTime. now());

        if(changePassword != null && !changePassword.equals(""))
            user.setPassword( BCrypt.hashpw(changePassword, BCrypt.gensalt(10)) );

        try {
            userDAO.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("User was updated successfully");
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while updating the user");
        }
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userDAO.findByUsername(username);
    }

    @Override
    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}
