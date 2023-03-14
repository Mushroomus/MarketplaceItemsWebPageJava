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
    private ItemService itemService;
    private UserService userService;
    private ItemImageService itemImageService;

    private ItemListService itemListService;

    private Validator validator;

    private ObjectMapper objectMapper = new ObjectMapper();

    public MessageController(MessageService theMessageService, ItemService theItemService, UserService theUserService, ItemListService theItemListService, ItemImageService theItemImageService){

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        messageService = theMessageService;
        itemService = theItemService;
        userService = theUserService;
        itemListService = theItemListService;
        itemImageService = theItemImageService;


        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @GetMapping("list")
    public String messageList() {
        return "messages/show-messages-admin";
    }


    @Transactional
    @GetMapping("accept")
    public ResponseEntity<String> acceptMessage(@RequestParam(name="messageId") Long id) {

        Optional<Message> theMessageOptional = messageService.findById(id);

        Message theMessage;

        if(theMessageOptional.isPresent()) {
            theMessage = theMessageOptional.get();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Something went wrong" + "\" }");
        }

        try {
            switch (theMessage.getMessageType()) {
                case "add":
                    String skuPrefix = theMessage.getSku().split(";")[0];
                    String image_url = "";

                    if (skuPrefix.matches("\\d+")) {
                        Integer shortenSku = Integer.parseInt(theMessage.getSku().split(";")[0]);
                        image_url = itemImageService.findByDefindexReturnUrl(shortenSku);
                    }

                    Item theAddItem = Item.builder()
                            .sku(theMessage.getSku())
                            .name(theMessage.getName())
                            .marketplacePrice(theMessage.getMarketplacePrice())
                            .craftable(theMessage.getCraftable())
                            .classItem(theMessage.getItemClass())
                            .quality(theMessage.getQuality())
                            .type(theMessage.getType())
                            .image(image_url)
                            .build();

                    itemService.saveItem(theAddItem);
                    messageService.deleteById(theMessage.getId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Request was accepted" + "\" }");

                case "update":
                    Item theUpdateItem = Item.builder()
                            .sku(theMessage.getSku())
                            .name(theMessage.getName())
                            .marketplacePrice(theMessage.getMarketplacePrice())
                            .craftable(theMessage.getCraftable())
                            .classItem(theMessage.getItemClass())
                            .quality(theMessage.getQuality())
                            .type(theMessage.getType())
                            .image(theMessage.getItem().getImage())
                            .build();

                    itemService.saveItem(theUpdateItem);
                    messageService.deleteById(theMessage.getId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Request was accepted" + "\" }");

                case "updatePrice":
                    String sku = theMessage.getItem().getSku();
                    Double price = theMessage.getMarketplacePrice();
                    itemService.updateMarketplacePriceBySku(sku, price);
                    messageService.deleteById(theMessage.getId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Request was accepted" + "\" }");

                case "delete":
                    String skuToDelete = theMessage.getItem().getSku();
                    messageService.deleteById(theMessage.getId());
                    itemListService.deleteAllByItemSku(skuToDelete);
                    messageService.deleteAllByItemSku(skuToDelete);
                    itemService.deleteBySku(skuToDelete);
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Request was accepted" + "\" }");

                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Invalid message type" + "\" }");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Something went wrong" + "\" }");
        }

    }

    @GetMapping("reject")
    public ResponseEntity<String> rejectMessage(@RequestParam(name="messageId") Long id) {
        try {
            messageService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Request was rejected" + "\" }");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Something went wrong" + "\" }");
        }
    }

    @GetMapping("fetch")
    public ResponseEntity<String> getAllMessagesWithPagination(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "4") int size,
                                                               @RequestParam(value = "search", required = false) String search,
                                                               @RequestParam(value = "types", required = false) String types,
                                                               @RequestParam(value = "startDate", required = false) String startDate,
                                                               @RequestParam(value = "endDate", required = false) String endDate) {

        Pageable paging = PageRequest.of(page, size);

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

        List<String> typeList = null;

        if(types != null && !types.equals(""))
            typeList = Arrays.asList(types.split(","));

        Page<Message> messagesPage = messageService.findAll(paging, search, typeList, start, end);

        List<Message> editedMessages = messagesPage.stream()
                .map(message -> {

                    if (message.getUser() != null) {
                        message.setUsername(message.getUser().getUsername());
                        message.setUser(null);
                    }
                    return message;
                })
                .collect(Collectors.toList());

        int currentPage = messagesPage.getNumber();
        int totalPages = messagesPage.getTotalPages();
        long totalItems = messagesPage.getTotalElements();

        MessagesResponse response = new MessagesResponse();
        response.setMessages(editedMessages);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalItems);
        response.setCurrentPage(currentPage);

        try {
            String jsonResponse = objectMapper.writeValueAsString(response);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);

        } catch (JsonProcessingException e) {
            String errorMessage = "An error occurred while processing the JSON response";
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + errorMessage + "\" }");
        }

    }

    public class MessagesResponse {
        private int totalPages;
        private long totalElements;

        private int currentPage;
        private List<Message> messages;


        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
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

        request.setDate(LocalDateTime.now());

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
