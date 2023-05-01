package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.ListDetails;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ListDAO extends JpaRepository<ListDetails, Long>, JpaSpecificationExecutor<ListDetails> {

    ListDetails findListByName(String name);

    ListDetails findListByNameAndUser(String name, User user);
}
