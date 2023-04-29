package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface MessageService {
    void saveMessage(Message theMessage);
    void deleteAllByItemSku(String itemSku);
    void deleteAllByUserId(int id_user);
    void deleteById(Long id);
    Optional<Message> findById(Long id);
    ResponseEntity<String> acceptMessage(Long messageId);
    ResponseEntity<String> rejectMessage(Long messageId);
    ResponseEntity<String> getMessages(int page, int size, String search, String types, String startDate, String endDate);
    ResponseEntity<String> createMessage(Message request, String itemSku);
}
