package com.marketplace.MarketplaceItems.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.marketplace.MarketplaceItems.entity.Sale;

public interface SaleDAO extends JpaRepository<Sale, Integer>, JpaSpecificationExecutor<Sale> {

}
