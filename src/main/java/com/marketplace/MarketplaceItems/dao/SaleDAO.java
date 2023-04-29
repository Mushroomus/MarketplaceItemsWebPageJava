package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleDAO extends JpaRepository<Sale, Integer>, JpaSpecificationExecutor<Sale> {

    @Query("SELECT DISTINCT YEAR(s.date) FROM Sale s WHERE s.user = :user")
    List<String> getDistinctYears(@Param("user") User user);

    @Modifying
    @Query("UPDATE Sale SET item = null WHERE item = :item")
    void updateItemDeletedNull(@Param("item") Item item);

    @Modifying
    @Query("UPDATE Sale SET item = :item WHERE assignSku = :sku")
    void updateSkuNewAddedItem(@Param("item") Item item, @Param("sku") String sku);

    void deleteAllByUserId(int user_id);

    List<Sale> findByUser(User user);
}
