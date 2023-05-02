package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.ItemList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemListDAO extends JpaRepository<ItemList, Long> {
    List<ItemList> findByListId(Long id_list);

    List<ItemList> findByUserIdAndListId(int id_user, Long id_list);

    List<ItemList> findByUserId(int id_user);
}
