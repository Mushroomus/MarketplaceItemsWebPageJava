package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Message;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.ItemListService;
import com.marketplace.MarketplaceItems.service.ItemService;
import com.marketplace.MarketplaceItems.service.MessageService;
import com.marketplace.MarketplaceItems.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private MessageService messageService;
    private ItemService itemService;
    private UserService userService;


    private Validator validator;

    public MessageController(MessageService theMessageService, ItemService theItemService, UserService theUserService ){

        messageService = theMessageService;
        itemService = theItemService;
        userService = theUserService;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }



    @PostMapping("create")
    public ResponseEntity<String> createMessage(@RequestBody Message request, @RequestParam(name="sku", required = false) String itemSku) {

        try{
            User user = userService.getCurrentUser();
            request.setUser(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Something went wrong\"}");
        }

        if(itemSku != null) {
            try {
                request.setItem(itemService.findItemBySku(itemSku));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Something went wrong\"}");
            }
        }

        Set<ConstraintViolation<Message>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            // handle validation errors
            String errorMessage = violations.stream()
                    .map(violation -> violation.getMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            messageService.saveMessage(request);
            return ResponseEntity.ok().body("{\"message\": \"Request was sent\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Something went wrong\"}");
        }
    }

}
