package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Message;
import com.marketplace.MarketplaceItems.model.GetMessagesResponse;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface MessageService {
    ResponseEntity<ResponseMessage> acceptMessage(Long messageId);
    ResponseEntity<ResponseMessage> rejectMessage(Long messageId);
    ResponseEntity<GetMessagesResponse> getMessages(int page, int size, String search, String types, String startDate, String endDate);
    ResponseEntity<ResponseMessage> createMessage(Message request, String itemSku);
}
