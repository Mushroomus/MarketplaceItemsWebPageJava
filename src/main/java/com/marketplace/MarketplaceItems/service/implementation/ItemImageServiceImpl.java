package com.marketplace.MarketplaceItems.service.implementation;

import com.marketplace.MarketplaceItems.dao.ItemImageDAO;
import com.marketplace.MarketplaceItems.service.ItemImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemImageServiceImpl implements ItemImageService {

    private ItemImageDAO itemImageDAO;

    @Autowired
    public ItemImageServiceImpl(ItemImageDAO theItemImageDAO) {
        itemImageDAO = theItemImageDAO;
    }

    public String findByDefindexReturnUrl(Integer defindex) {
        return itemImageDAO.findByDefindexReturnUrl(defindex);
    }
}
