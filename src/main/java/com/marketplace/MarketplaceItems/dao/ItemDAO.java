package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.function.Predicate;

public interface ItemDAO extends JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {
    //Page<Item> findAll(Pageable pageable, Predicate<Item> filter);

}
