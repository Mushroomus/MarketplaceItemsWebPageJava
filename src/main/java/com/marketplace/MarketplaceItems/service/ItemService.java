package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.model.ResponseMessage;
import org.springframework.data.domain.Page;
import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ItemService {
    ResponseEntity<PagedModel<Item>> getItems(int page, int size, String search, String craftable, List<String> classes, List<String> qualities, List<String> types);
    ResponseEntity<ResponseMessage> addItem(Item item);
    ResponseEntity<ResponseMessage> deleteItem(String itemSku);
    ResponseEntity<String> updateItem(Item item);
    ResponseEntity<String> updateItemPrice( Map<String, Object> requestBody);
}
