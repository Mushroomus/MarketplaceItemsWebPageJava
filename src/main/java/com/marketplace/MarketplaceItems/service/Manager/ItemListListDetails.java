package com.marketplace.MarketplaceItems.service.Manager;

import com.marketplace.MarketplaceItems.entity.ListDetails;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface ItemListListDetails {
    ListDetails findListByName(String name);
    void deleteList(ListDetails listDetails);
    void saveList(ListDetails theListDetails);
    ListDetails findListByNameAndUser(String name, User user);
}
