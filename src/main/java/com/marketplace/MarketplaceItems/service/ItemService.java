package com.marketplace.MarketplaceItems.service;

import org.springframework.data.domain.Page;
import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {

    public List<Item> findAll();

    public Page<Item> findAll(Pageable page, List<String> classes);

    public void saveItem(Item item);

    public void deleteBySku(String sku);

    public void updateItem(Item item);

}
