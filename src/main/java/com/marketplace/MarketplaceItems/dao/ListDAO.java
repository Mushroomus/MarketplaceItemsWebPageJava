package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.List;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ListDAO extends JpaRepository<List, Long>, JpaSpecificationExecutor<List> {

    List findListByName(String name);

    List findListByNameAndUser(String name, User user);
}
