package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.UserDAO;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO theUserDAO) {
        userDAO = theUserDAO;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userDAO.findAll(pageable);
    };

    @Override
    public void saveUser(User user) { userDAO.save(user); }

    @Override
    public void deleteUserById(Integer id) { userDAO.deleteById(id); }

    @Override
    public void updateUser(User user) { userDAO.save(user); }

    @Override
    public User findByUsername(String username) { return userDAO.findByUsername(username); }

}
