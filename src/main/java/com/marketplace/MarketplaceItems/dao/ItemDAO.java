package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.function.Predicate;

public interface ItemDAO extends JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {
    Page<Item> findByNameLikeIgnoreCaseOrSkuLikeIgnoreCase(String name, String sku, Pageable pageable);

    Item findItemBySku(String sku);

    @Modifying
    @Query("UPDATE Item i SET i.marketplacePrice = :marketplacePrice WHERE i.sku = :sku")
    void updateMarketplacePriceBySku(@Param("sku") String sku, @Param("marketplacePrice") double marketplacePrice);
}
