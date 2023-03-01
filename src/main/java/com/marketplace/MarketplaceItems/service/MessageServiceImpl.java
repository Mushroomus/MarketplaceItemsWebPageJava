package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ListDAO;
import com.marketplace.MarketplaceItems.dao.MessageDAO;
import com.marketplace.MarketplaceItems.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService{

    private MessageDAO messageDAO;

    @Autowired
    public  MessageServiceImpl(MessageDAO theMessageDAO) {
        messageDAO = theMessageDAO;
    }

    @Override
    public void saveMessage(Message theMessage) {
        messageDAO.save(theMessage);
    }

    @Override
    public void deleteAllByItemSku(String itemSku) {
        messageDAO.deleteAllByItemSku(itemSku);
    }

    @Override
    public void deleteAllByUserId(int id_user) {
        messageDAO.deleteAllByUserId(id_user);
    }

}
