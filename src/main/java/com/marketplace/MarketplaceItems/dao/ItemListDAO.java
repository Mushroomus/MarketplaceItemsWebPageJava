package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.ItemList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemListDAO extends JpaRepository<ItemList, Long> {
}
