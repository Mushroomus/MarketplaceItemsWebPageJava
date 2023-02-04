package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemDAO extends JpaRepository<Item, String> {
}
