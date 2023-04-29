package com.marketplace.MarketplaceItems.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Message;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService theMessageService){
        messageService = theMessageService;
    }

    @GetMapping("list")
    public String showMessageList() {
        return "messages/show-messages-admin";
    }


    @Transactional
    @GetMapping("{messageId}/accept")
    public ResponseEntity<String> acceptMessage(@PathVariable("messageId") Long messageId) {
        return messageService.acceptMessage(messageId);
    }

    @GetMapping("{messageId}/reject")
    public ResponseEntity<String> rejectMessage(@PathVariable("messageId") Long messageId) {
       return messageService.rejectMessage(messageId);
    }

    @GetMapping
    public ResponseEntity<String> getMessages(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "4") int size,
                                                               @RequestParam(value = "search", required = false) String search,
                                                               @RequestParam(value = "types", required = false) String types,
                                                               @RequestParam(value = "startDate", required = false) String startDate,
                                                               @RequestParam(value = "endDate", required = false) String endDate) {
        return messageService.getMessages(page, size, search, types, startDate, endDate);
    }

    @PostMapping
    public ResponseEntity<String> createMessage(@RequestBody Message request, @RequestParam(name="sku", required = false) String itemSku) {
        return messageService.createMessage(request, itemSku);
    }
}
