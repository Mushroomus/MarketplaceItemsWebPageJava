package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SaleService {
    Page<Sale> findAll(Pageable pageable);

    void saveAll(List<Sale> sales);
}
