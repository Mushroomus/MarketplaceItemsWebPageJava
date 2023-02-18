package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ItemListDAO;
import com.marketplace.MarketplaceItems.entity.ItemList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemListServiceImpl implements ItemListService{

    private ItemListDAO itemListDAO;

    @Autowired
    public ItemListServiceImpl(ItemListDAO theItemListDAO) {
        itemListDAO = theItemListDAO;
    }


    @Override
    public void saveItemList(ItemList theItemList) {
        itemListDAO.save(theItemList);
    }

}
