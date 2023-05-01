package com.marketplace.MarketplaceItems.service.operation;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

@Service
public interface MessageItemOperations {
    void saveItem(Item item);
    void updateMarketplacePriceBySku(String sku, Double marketplacePrice);
    void deleteBySku(String sku);
    Item findItemBySku(String sku);
}
