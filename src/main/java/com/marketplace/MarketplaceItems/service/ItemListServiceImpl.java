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
    public java.util.List<ItemList> findByListId(Long listId) {
        return itemListDAO.findByListId(listId);
    }

    @Override
    public java.util.List<ItemList> findByUsernameAndListId(int userId, Long listId) {
        return itemListDAO.findByUserIdAndListId(userId, listId);
    }

    @Override
    public void saveItemList(ItemList theItemList) {
        itemListDAO.save(theItemList);
    }

    @Override
    public void deleteAllByUserId(int id_user) { itemListDAO.deleteAllByUserId(id_user); }

}
