package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.ItemList;
import com.marketplace.MarketplaceItems.entity.List;
import org.springframework.stereotype.Service;

@Service
public interface ItemListService {

    public java.util.List<ItemList> findByListId(Long listId);

    public java.util.List<ItemList> findByUsernameAndListId(int userId, Long listId);

    public void saveItemList(ItemList theItemList);

}
