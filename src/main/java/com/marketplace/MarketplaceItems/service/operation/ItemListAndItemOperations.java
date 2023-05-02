package com.marketplace.MarketplaceItems.service.operation;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemListAndItemOperations {
    ResponseEntity<PagedModel<Item>> getItems(int page, int size, String search, String craftable, List<String> classes, List<String> qualities, List<String> types);
    Item findItemBySku(String sku);
}
