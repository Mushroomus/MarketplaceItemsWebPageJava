package com.marketplace.MarketplaceItems.service.operation;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

@Service
public interface MessageSaleOperations {
    void updateSkuNewAddedItem(Item item, String sku);
    void updateItemDeletedNull(Item item);
}
