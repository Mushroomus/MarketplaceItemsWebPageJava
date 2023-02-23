package com.marketplace.MarketplaceItems.dao;

import com.marketplace.MarketplaceItems.entity.ItemList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemListDAO extends JpaRepository<ItemList, Long> {

    java.util.List<ItemList> findByListId(Long id_list);

    java.util.List<ItemList> findByUserIdAndListId(int id_user, Long id_list);

    java.util.List<ItemList> findByUserId(int id_user);

    public void deleteAllByUserId(int id_user);
}
