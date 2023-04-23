package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ListDAO;
import com.marketplace.MarketplaceItems.entity.List;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ListServiceImpl implements ListService {


    private ListDAO listDAO;

    @Autowired
    public ListServiceImpl(ListDAO theListDAO) {
        listDAO = theListDAO;
    }

    @Override
    public List findListByName(String name) { return listDAO.findListByName(name); }

    @Override
    public void deleteList(List list) { listDAO.delete(list); }


    @Override
    public void saveList(List theList) { listDAO.save(theList); }

    @Override
    public List findListByNameAndUser(String name, User user) {
        return listDAO.findListByNameAndUser(name, user);
    }

    ;
}
