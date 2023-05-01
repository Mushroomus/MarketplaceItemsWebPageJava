package com.marketplace.MarketplaceItems.service.Manager;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

@Service
public interface SaleItem {
    Item findItemBySku(String sku);
}
