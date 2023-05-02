package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.Message;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageDAO extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
    Message save(Message theMessage);
    @Query("SELECT m FROM Message m " +
            "JOIN m.item i " +
            "JOIN m.user u " +
            "WHERE (:search IS NULL OR i.name LIKE CONCAT('%', :search, '%') OR u.username LIKE CONCAT('%', :search, '%')) " +
            "AND (:types IS NULL OR m.messageType IN (:types)) " +
            "AND (:startDate IS NULL OR m.date >= :startDate) " +
            "AND (:endDate IS NULL OR m.date <= :endDate)")
    Page<Message> findAll(String search, List<String> types, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
