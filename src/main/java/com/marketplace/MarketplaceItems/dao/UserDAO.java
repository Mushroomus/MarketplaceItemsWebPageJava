package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserDAO extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u " +
            "WHERE (:search IS NULL OR u.username LIKE CONCAT('%', :search, '%')) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:startDate IS NULL OR u.date >= :startDate) " +
            "AND (:endDate IS NULL OR u.date <= :endDate) " +
            "AND u.id != :currentUserId")
    Page<User> findAll(@Param("search") String search,
                       @Param("role") String role,
                       @Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate,
                       @Param("currentUserId") Integer currentUserId,
                       Pageable pageable);
}
