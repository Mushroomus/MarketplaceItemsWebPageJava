package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ItemDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void updateItem(Item item) {
        Optional<Item> searchItem = itemDAO.findById(item.getSku());

        if(searchItem.isPresent()) {
            Item updateItem = searchItem.get();
            updateItem.setName(item.getName());
            updateItem.setCraftable(item.isCraftable());
            updateItem.setClassItem(item.getClassItem());
            updateItem.setQuality(item.getQuality());
            updateItem.setType(item.getType());
            updateItem.setImage(item.getImage());
            itemDAO.save(updateItem);
        } else {
            System.out.println("Not Found");
        }
    }

}
