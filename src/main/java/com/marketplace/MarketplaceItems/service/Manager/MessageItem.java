package com.marketplace.MarketplaceItems.service.Manager;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

@Service
public interface MessageItem {
    void saveItem(Item item);
    void updateMarketplacePriceBySku(String sku, Double marketplacePrice);
    void deleteBySku(String sku);
    Item findItemBySku(String sku);
}
