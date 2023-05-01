package com.marketplace.MarketplaceItems.service.Manager;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

@Service
public interface ItemSale {
    void updateSkuNewAddedItem(Item item, String sku);
    void updateItemDeletedNull(Item item);
}
