package com.marketplace.MarketplaceItems.service.operation;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

@Service
public interface SaleItemOperations {
    Item findItemBySku(String sku);
}
