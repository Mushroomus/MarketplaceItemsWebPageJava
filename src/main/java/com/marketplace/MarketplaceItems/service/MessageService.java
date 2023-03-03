package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MessageService {

    void saveMessage(Message theMessage);

    void deleteAllByItemSku(String itemSku);

    void deleteAllByUserId(int id_user);

    Page<Message> findAll(Pageable page);

    void deleteById(Long id);

    Optional<Message> findById(Long id);
}
