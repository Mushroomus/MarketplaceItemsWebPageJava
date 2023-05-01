package com.marketplace.MarketplaceItems.service.implementation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.MarketplaceItems.dao.MessageDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Message;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.model.MessagesResponse;
import com.marketplace.MarketplaceItems.service.ItemImageService;
import com.marketplace.MarketplaceItems.service.MessageService;
import com.marketplace.MarketplaceItems.service.operation.MessageItemOperations;
import com.marketplace.MarketplaceItems.service.operation.MessageSaleOperations;
import com.marketplace.MarketplaceItems.service.operation.MessageUserOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageDAO messageDAO;
    private ItemImageService itemImageService;
    private MessageItemOperations messageItemOperations;
    private MessageSaleOperations messageSaleOperations;
    private MessageUserOperations messageUserOperations;
    private Validator validator;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public  MessageServiceImpl(MessageDAO theMessageDAO, ItemImageService theItemImageService, MessageItemOperations theMessageItemOperations, MessageSaleOperations theMessageSaleOperations, MessageUserOperations theMessageUserOperations) {
        messageDAO = theMessageDAO;
        itemImageService = theItemImageService;
        messageItemOperations = theMessageItemOperations;
        messageSaleOperations = theMessageSaleOperations;
        messageUserOperations = theMessageUserOperations;

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public ResponseEntity<String> acceptMessage(Long messageId) {

        Optional<Message> theMessageOptional = messageDAO.findById(messageId);

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

                    messageItemOperations.saveItem(theAddItem);
                    messageSaleOperations.updateSkuNewAddedItem(theAddItem, theAddItem.getSku());
                    messageDAO.deleteById(theMessage.getId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Request was accepted" + "\" }");

                case "update":
                    Item theUpdateItem = Item.builder()
                            .sku(theMessage.getItem().getSku())
                            .name(theMessage.getName())
                            .craftable(theMessage.getCraftable())
                            .classItem(theMessage.getItemClass())
                            .quality(theMessage.getQuality())
                            .marketplacePrice(theMessage.getItem().getMarketplacePrice())
                            .type(theMessage.getType())
                            .image(theMessage.getItem().getImage())
                            .build();

                    messageItemOperations.saveItem(theUpdateItem);
                    messageDAO.deleteById(theMessage.getId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Request was accepted" + "\" }");

                case "updatePrice":
                    String sku = theMessage.getItem().getSku();
                    Double price = theMessage.getMarketplacePrice();
                    messageItemOperations.updateMarketplacePriceBySku(sku, price);
                    messageDAO.deleteById(theMessage.getId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{ \"error\": \"" + "Request was accepted" + "\" }");

                case "delete":
                    String skuToDelete = theMessage.getItem().getSku();
                    messageDAO.deleteById(theMessage.getId());

                    /* changed to cascade
                    itemListService.deleteAllByItemSku(skuToDelete);
                    messageService.deleteAllByItemSku(skuToDelete);
                     */

                    messageSaleOperations.updateItemDeletedNull(theMessage.getItem());
                    messageItemOperations.deleteBySku(skuToDelete);
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

    @Override
    public ResponseEntity<String> rejectMessage(Long messageId) {
        try {
            messageDAO.deleteById(messageId);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Request was rejected" + "\" }");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Something went wrong" + "\" }");
        }
    }

    private Page<Message> findAll(Pageable pageable, String search, List<String> types, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<Message> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                predicates.add(builder.or(
                        builder.like(root.get("item").get("name"), searchPattern),
                        builder.like(root.get("user").get("username"), searchPattern)
                ));
                System.out.println(search);
            }
            if (types != null && !types.isEmpty()) {
                predicates.add(builder.in(root.get("messageType")).value(types));
                System.out.println(types);
            }
            if (startDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
                System.out.println(startDate);
            }
            if (endDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("date"), endDate));
                System.out.println(endDate);
            }
            return predicates.isEmpty() ? builder.conjunction() : builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
        };

        return messageDAO.findAll(spec, pageable);
    }

    @Override
    public ResponseEntity<String> getMessages(int page, int size, String search, String types, String startDate, String endDate) {

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

        Page<Message> messagesPage = findAll(paging, search, typeList, start, end);

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

    @Override
    public ResponseEntity<String> createMessage(Message request, String itemSku) {

        try{
            User user = messageUserOperations.getCurrentUser();
            request.setUser(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Something went wrong\"}");
        }

        if(itemSku != null) {
            try {
                request.setItem(messageItemOperations.findItemBySku(itemSku));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Something went wrong\"}");
            }
        }

        request.setDate(LocalDateTime.now());

        Set<ConstraintViolation<Message>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            messageDAO.save(request);
            return ResponseEntity.ok().body("{\"message\": \"Request was sent\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Something went wrong\"}");
        }
    }
}
