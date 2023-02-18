package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ListDAO;
import com.marketplace.MarketplaceItems.entity.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListServiceImpl implements ListService {

    private ListDAO listDAO;

    @Autowired
    public ListServiceImpl(ListDAO theListDAO) {
        listDAO = theListDAO;
    }

    @Override
    public void saveList(List theList) { listDAO.save(theList); };
}
