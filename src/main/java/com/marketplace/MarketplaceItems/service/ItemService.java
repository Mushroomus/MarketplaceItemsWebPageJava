package com.marketplace.MarketplaceItems.service;

import org.springframework.data.domain.Page;
import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {

    public Page<Item> findAll(Pageable page, String craftable, List<String> classes, List<String> qualities, List<String> types);

    public Page<Item> findAll(Pageable page, String search);

    public Page<Item> findAllFilters(Pageable pageable, String search, String craftable, List<String> classes, List<String> qualities, List<String> types);

    public void saveItem(Item item);

    public void deleteBySku(String sku);

    public void updateItem(Item item);

    public Item findItemBySku(String sku);
}
