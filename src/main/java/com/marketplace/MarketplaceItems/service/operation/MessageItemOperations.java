package com.marketplace.MarketplaceItems.service.operation;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

@Service
public interface MessageItemOperations {
    void saveItem(Item item);
    void updateItemMarketplacePriceBySku(String sku, Double marketplacePrice);
    void deleteItemBySku(String sku);
    Item findItemBySku(String sku);
}
