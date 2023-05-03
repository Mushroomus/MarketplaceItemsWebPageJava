package com.marketplace.MarketplaceItems.service.operation;

import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface ItemListUserOperations {
    User getCurrentUser();
    User findUserByUsername(String username);
}
