package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.ItemList;
import org.springframework.stereotype.Service;

@Service
public interface ItemListService {

    public void saveItemList(ItemList theItemList);

}
