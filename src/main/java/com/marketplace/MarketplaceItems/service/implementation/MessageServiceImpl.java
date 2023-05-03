package com.marketplace.MarketplaceItems.service.implementation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.MarketplaceItems.dao.MessageDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Message;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.exception.BadRequestException;
import com.marketplace.MarketplaceItems.exception.InternalServerErrorException;
import com.marketplace.MarketplaceItems.model.GetMessagesResponse;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import com.marketplace.MarketplaceItems.service.ItemImageService;
import com.marketplace.MarketplaceItems.service.MessageService;
import com.marketplace.MarketplaceItems.service.operation.MessageItemOperations;
import com.marketplace.MarketplaceItems.service.operation.MessageSaleOperations;
import com.marketplace.MarketplaceItems.service.operation.MessageUserOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public  MessageServiceImpl(MessageDAO theMessageDAO, @Qualifier("itemImageServiceImpl") ItemImageService theItemImageService, @Qualifier("itemServiceImpl") MessageItemOperations theMessageItemOperations, @Qualifier("saleServiceImpl") MessageSaleOperations theMessageSaleOperations, @Qualifier("userServiceImpl") MessageUserOperations theMessageUserOperations) {
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
    public ResponseEntity<ResponseMessage> acceptMessage(Long messageId) {

        Optional<Message> theMessageOptional = messageDAO.findById(messageId);

        Message theMessage;

        if(theMessageOptional.isPresent()) {
            theMessage = theMessageOptional.get();
        } else {
            throw new InternalServerErrorException("Message is missing");
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
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Request was accepted"));

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
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Request was accepted"));

                case "updatePrice":
                    String sku = theMessage.getItem().getSku();
                    Double price = theMessage.getMarketplacePrice();
                    messageItemOperations.updateItemMarketplacePriceBySku(sku, price);
                    messageDAO.deleteById(theMessage.getId());
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Request was accepted"));

                case "delete":
                    String skuToDelete = theMessage.getItem().getSku();
                    messageDAO.deleteById(theMessage.getId());

                    /* changed to cascade
                    itemListService.deleteAllByItemSku(skuToDelete);
                    messageService.deleteAllByItemSku(skuToDelete);
                     */

                    messageSaleOperations.updateItemDeletedNull(theMessage.getItem());
                    messageItemOperations.deleteItemBySku(skuToDelete);
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Request was accepted"));

                default:
                    throw new BadRequestException("Invalid message type");
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong");
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> rejectMessage(Long messageId) {
        try {
            messageDAO.deleteById(messageId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Request was rejected"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occured while rejecting message");
        }
    }

    private LocalDateTime parseDate(String dateString) {
        if (dateString == null || dateString.equals("null")) {
            return null;
        }
        long timestamp = Long.parseLong(dateString);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    @Override
    public ResponseEntity<GetMessagesResponse> getMessages(int page, int size, String search, String types, String startDate, String endDate) {

        Pageable paging = PageRequest.of(page, size);
        LocalDateTime start = parseDate(startDate);
        LocalDateTime end = parseDate(endDate);

        List<String> typeList = null;

        if(types != null && !types.equals(""))
            typeList = Arrays.asList(types.split(","));

        Page<Message> messagesPage = messageDAO.findAll(search, typeList, start, end, paging);

        List<Message> editedMessages = messagesPage.stream()
                .peek(message -> {
                    if (message.getUser() != null) {
                        message.setUsername(message.getUser().getUsername());
                        message.setUser(null);
                    }
                })
                .collect(Collectors.toList());

        int currentPage = messagesPage.getNumber();
        int totalPages = messagesPage.getTotalPages();
        long totalItems = messagesPage.getTotalElements();

        GetMessagesResponse response = GetMessagesResponse
                                        .builder()
                                        .messages(editedMessages)
                                        .totalPages(totalPages)
                                        .totalElements(totalItems)
                                        .currentPage(currentPage)
                                        .build();
        try {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new InternalServerErrorException("An error occurred while processing the JSON response");
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> createMessage(Message request, String itemSku) {

        try {
            User user = messageUserOperations.getCurrentUser();
            request.setUser(user);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occured while setting user");
        }

        if(itemSku != null) {
            try {
                request.setItem(messageItemOperations.findItemBySku(itemSku));
            } catch (Exception e) {
                throw new InternalServerErrorException("Error occured while setting item");
            }
        }

        request.setDate(LocalDateTime.now());

        Set<ConstraintViolation<Message>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(errorMessage);
        }

        try {
            messageDAO.save(request);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Message was created"));
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occured while saving message");
        }
    }
}
