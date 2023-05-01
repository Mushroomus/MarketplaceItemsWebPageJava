package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.ItemList;
import com.marketplace.MarketplaceItems.model.CreateListRequest;
import com.marketplace.MarketplaceItems.model.EditListRequest;
import com.marketplace.MarketplaceItems.model.ItemRequestResult;
import com.marketplace.MarketplaceItems.model.ResponseMessage;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Service
public interface ItemListService {
    ResponseEntity<PagedModel<Item>> getItemList(int page, int size, String search, String craftable, java.util.List<String> classes, java.util.List<String> qualities, java.util.List<String> types);
    Map<String, Object> getListInfoAndButtonStatus();
    void deleteItemList(String name);
    Map<String, Object> getCreateListTableData();
    ResponseEntity<ResponseMessage> createList(CreateListRequest request);
    ResponseEntity<ResponseMessage> saveEditedList(EditListRequest request);
    ResponseEntity<List<Item>> fetchRightList(@RequestParam(value="listName") String name);
    List<ItemRequestResult> priceItems(String name, String marketplaceKeyPrice);
}

