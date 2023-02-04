package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {

    public List<Item> findAll();

    public void saveItem(Item item);

    public void deleteBySku(String sku);

}
