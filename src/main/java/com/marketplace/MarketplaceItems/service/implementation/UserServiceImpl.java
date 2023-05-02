package com.marketplace.MarketplaceItems.service.implementation;

import com.marketplace.MarketplaceItems.dao.UserDAO;
import com.marketplace.MarketplaceItems.entity.User;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        if(ifUpdate != true) {
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
            return new ResponseEntity<>(new ResponseMessage("User was added"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Error occured while adding an user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    private Page<User> findAll(Pageable pageable, String search, String role, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<User> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                predicates.add(builder.like(root.get("username"), searchPattern  ));
            }
            if (role != null && !role.isEmpty()) {
                predicates.add(builder.equal(root.get("role"), role));
            }
            if (startDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("date"), endDate));
            }
            predicates.add(builder.notEqual(root.get("id"), getCurrentUser().getId()));

            return predicates.isEmpty() ? builder.conjunction() : builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
        };

        return userDAO.findAll(spec, pageable);
    }
     */

    @Override
    public ResponseEntity<PagedModel<User>> getUserList(int page, int size, String search, String role, String startDate, String endDate) {
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

        users =  userDAO.findAll(search, role, start, end, getCurrentUser().getId(), pageable);
        PagedModel<User> pagedModel = PagedModel.of(users.getContent(), new PagedModel.PageMetadata(users.getSize(), users.getNumber(), users.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
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
            return new ResponseEntity<>(new ResponseMessage("User was deleted"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Failed to delete user"), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<String> updateUser(UpdateUserRequest request) {

        User user = request.getUser();
        String changePassword = request.getChangePassword();

        String resultValidation = validateUser(user, changePassword, true);

        if (!resultValidation.equals("valid")) {
            return new ResponseEntity<>(resultValidation, HttpStatus.BAD_REQUEST);
        }

        user.setDate(LocalDateTime. now());

        if(changePassword != null && !changePassword.equals(""))
            user.setPassword( BCrypt.hashpw(changePassword, BCrypt.gensalt(10)) );

        try {
            userDAO.save(user);
            return new ResponseEntity<>("User was updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while updating the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userDAO.findByUsername(username);
    }

    @Override
    public User findByUsername(String username) { return userDAO.findByUsername(username); }
}
