package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ListDAO;
import com.marketplace.MarketplaceItems.entity.ListDetails;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.Manager.ItemListListDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListServiceImpl implements ItemListListDetails {
    private ListDAO listDAO;

    @Autowired
    public ListServiceImpl(ListDAO theListDAO) {
        listDAO = theListDAO;
    }

    @Override
    public ListDetails findListByName(String name) { return listDAO.findListByName(name); }

    @Override
    public void deleteList(ListDetails listDetails) { listDAO.delete(listDetails); }


    @Override
    public void saveList(ListDetails theListDetails) { listDAO.save(theListDetails); }

    @Override
    public ListDetails findListByNameAndUser(String name, User user) {
        return listDAO.findListByNameAndUser(name, user);
    }

    ;
}
