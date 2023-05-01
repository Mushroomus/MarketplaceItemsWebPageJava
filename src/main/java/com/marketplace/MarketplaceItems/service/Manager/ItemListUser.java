package com.marketplace.MarketplaceItems.service.Manager;

import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface ItemListUser {
    User getCurrentUser();
    User findByUsername(String username);
}
