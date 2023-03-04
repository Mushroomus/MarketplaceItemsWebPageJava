package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Message;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageDAO extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    public Message save(Message theMessage);

    public void deleteAllByItemSku(String sku_item);

    public void deleteAllByUserId(int id_user);

}
