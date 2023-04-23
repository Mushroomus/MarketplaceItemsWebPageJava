package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.List;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ListService {

    public List findListByName(String name);

    public void deleteList(List list);

    public void saveList(List theList);

    List findListByNameAndUser(String name, User user);
}
