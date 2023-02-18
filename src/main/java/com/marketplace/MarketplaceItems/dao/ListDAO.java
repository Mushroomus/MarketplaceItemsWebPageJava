package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListDAO extends JpaRepository<List, Long> {
}
