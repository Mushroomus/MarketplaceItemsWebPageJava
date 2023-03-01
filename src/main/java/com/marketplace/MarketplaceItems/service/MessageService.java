package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Message;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    public void saveMessage(Message theMessage);

    public void deleteAllByItemSku(String itemSku);

    public void deleteAllByUserId(int id_user);
}
