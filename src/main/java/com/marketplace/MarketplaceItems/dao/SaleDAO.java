package com.marketplace.MarketplaceItems.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleDAO extends JpaRepository<Sale, Integer>, JpaSpecificationExecutor<Sale> {

    @Query("SELECT DISTINCT YEAR(s.date) FROM Sale s")
    List<String> getDistinctYears();

}
