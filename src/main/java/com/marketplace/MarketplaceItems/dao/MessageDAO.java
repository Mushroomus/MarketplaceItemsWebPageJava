package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageDAO extends JpaRepository<Message, Long> {

    public Message save(Message theMessage);

    public void deleteAllByItemSku(String sku_item);

    public void deleteAllByUserId(int id_user);

}
