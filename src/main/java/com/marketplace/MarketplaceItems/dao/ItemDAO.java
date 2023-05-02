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
    Item findItemBySku(String sku);

    @Modifying
    @Query("UPDATE Item i SET i.marketplacePrice = :marketplacePrice WHERE i.sku = :sku")
    void updateMarketplacePriceBySku(@Param("sku") String sku, @Param("marketplacePrice") double marketplacePrice);

    @Query("SELECT i FROM Item i " +
            "WHERE (:search = '' OR i.name LIKE CONCAT('%', :search, '%')) " +
            "AND (:craftable = '' OR i.craftable = :craftable) " +
            "AND (:types IS NULL OR i.type IN (:types)) " +
            "AND (:classes IS NULL OR i.classItem IN (:classes)) " +
            "AND (:qualities IS NULL OR i.quality IN (:qualities))")
    Page<Item> findAllFilters(@Param("search") String search,
                              @Param("craftable") String craftable,
                              @Param("types") List<String> types,
                              @Param("classes") List<String> classes,
                              @Param("qualities") List<String> qualities,
                              Pageable pageable);
}
