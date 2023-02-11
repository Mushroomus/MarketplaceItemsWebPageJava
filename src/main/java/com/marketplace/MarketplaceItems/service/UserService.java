package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface UserService {

    public Page<User> findAll(Pageable pageable);

    public void saveUser(User user);

    public void deleteUserById(Integer id);

    public void updateUser(User user);

    public User findByUsername(String username);

    public Page<User> findAll(Pageable pageable, String search, String role, LocalDateTime startDate, LocalDateTime endDate);
}
