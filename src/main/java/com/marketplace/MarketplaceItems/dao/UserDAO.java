package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    User findByUsername(String username);
    Page<User> findAll(Pageable pageable);

}
