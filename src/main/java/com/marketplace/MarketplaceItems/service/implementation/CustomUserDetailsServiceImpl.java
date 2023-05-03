package com.marketplace.MarketplaceItems.service.implementation;

import com.marketplace.MarketplaceItems.components.CustomUserDetails;
import com.marketplace.MarketplaceItems.dao.UserDAO;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private UserDAO userDAO;
    
    @Autowired
    CustomUserDetailsServiceImpl(UserDAO theUserDAO) {
        userDAO = theUserDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userDAO.findByUsername(username);
        if(user == null) {
            throw new RecordNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }
}
