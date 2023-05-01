package com.marketplace.MarketplaceItems.service.operation;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemListAndItemOperations {
    Page<Item> findAllFilters(Pageable pageable, String search, String craftable, List<String> classes, List<String> qualities, List<String> types);
    Item findItemBySku(String sku);
}
