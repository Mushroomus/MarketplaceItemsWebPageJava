package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleDAO extends JpaRepository<Sale, Integer>, JpaSpecificationExecutor<Sale> {

    @Query("SELECT s FROM Sale s " +
            "LEFT JOIN s.item i " +
            "WHERE (:craftable IS NULL OR i.craftable = :craftable) " +
            "AND (:classes IS NULL OR i.classItem IN (:classes)) " +
            "AND (:qualities IS NULL OR i.quality IN (:qualities)) " +
            "AND (:types IS NULL OR i.type IN (:types)) " +
            "AND (:startDate IS NULL OR s.date >= :startDate) " +
            "AND (:endDate IS NULL OR s.date <= :endDate) " +
            "AND (:minPrice IS NULL OR s.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR s.price <= :maxPrice) " +
            "AND s.user = :user")
    Page<Sale> getSalesPagination(@Param("user") User user,
                                  @Param("craftable") Boolean craftable,
                                  @Param("classes") List<String> classes,
                                  @Param("qualities") List<String> qualities,
                                  @Param("types") List<String> types,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  @Param("minPrice") Double minPrice,
                                  @Param("maxPrice") Double maxPrice,
                                  Pageable pageable);

    @Query("SELECT COUNT(s) FROM Sale s LEFT JOIN s.item i " +
            "WHERE (:craftable IS NULL OR i.craftable = :craftable) " +
            "AND (:classes IS NULL OR i.classItem IN (:classes)) " +
            "AND (:qualities IS NULL OR i.quality IN (:qualities)) " +
            "AND (:types IS NULL OR i.type IN (:types)) " +
            "AND (:startDate IS NULL OR s.date >= :startDate) " +
            "AND (:endDate IS NULL OR s.date <= :endDate) " +
            "AND (:minPrice IS NULL OR s.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR s.price <= :maxPrice) " +
            "AND s.user = :user")
    long countSalesByFilters(@Param("user") User user,
                                    @Param("craftable") Boolean craftable,
                                    @Param("classes") List<String> classes,
                                    @Param("qualities") List<String> qualities,
                                    @Param("types") List<String> types,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("minPrice") Double minPrice,
                                    @Param("maxPrice") Double maxPrice);

    @Query("SELECT s FROM Sale s LEFT JOIN s.item i " +
            "WHERE (:craftable IS NULL OR i.craftable = :craftableValue) " +
            "AND (:classes IS NULL OR i.classItem IN :classes) " +
            "AND (:qualities IS NULL OR i.quality IN :qualities) " +
            "AND (:types IS NULL OR i.type IN :types) " +
            "AND (:startDate IS NULL OR s.date >= :startDate) " +
            "AND (:endDate IS NULL OR s.date <= :endDate) " +
            "AND (:minPrice IS NULL OR s.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR s.price <= :maxPrice) " +
            "AND s.user = :user")
    List<Sale> getSalesNoPagination(@Param("user") User user,
                              @Param("craftable") Boolean craftable,
                              @Param("classes") List<String> classes,
                              @Param("qualities") List<String> qualities,
                              @Param("types") List<String> types,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate,
                              @Param("minPrice") Double minPrice,
                              @Param("maxPrice") Double maxPrice);


    @Query("SELECT DISTINCT YEAR(s.date) FROM Sale s WHERE s.user = :user")
    List<String> getDistinctYears(@Param("user") User user);

    @Modifying
    @Query("UPDATE Sale SET item = null WHERE item = :item")
    void updateItemDeletedNull(@Param("item") Item item);

    @Modifying
    @Query("UPDATE Sale SET item = :item WHERE assignSku = :sku")
    void updateSkuNewAddedItem(@Param("item") Item item, @Param("sku") String sku);

    List<Sale> findByUser(User user);
}
