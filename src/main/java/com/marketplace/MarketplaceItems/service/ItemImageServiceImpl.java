package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ItemDAO;
import com.marketplace.MarketplaceItems.dao.ItemImageDAO;
import com.marketplace.MarketplaceItems.service.Manager.MessageItemImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemImageServiceImpl implements ItemImageService, MessageItemImage {

    private ItemImageDAO itemImageDAO;

    @Autowired
    public ItemImageServiceImpl(ItemImageDAO theItemImageDAO) {
        itemImageDAO = theItemImageDAO;
    }

    public String findByDefindexReturnUrl(Integer defindex) {
        return itemImageDAO.findByDefindexReturnUrl(defindex);
    }
}
