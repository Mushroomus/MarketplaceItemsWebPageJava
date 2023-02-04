package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ItemDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemDAO itemDAO;

    @Autowired
    public ItemServiceImpl(ItemDAO theItemDAO) {
        itemDAO = theItemDAO;
    }

    @Override
    public List<Item> findAll() {
        return itemDAO.findAll();
    };

    @Override
    public void saveItem(Item item) { itemDAO.save(item); };

    @Override
    public void deleteBySku(String sku) { itemDAO.deleteById(sku); }

}
