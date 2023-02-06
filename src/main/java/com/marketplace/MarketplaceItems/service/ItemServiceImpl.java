package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ItemDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemDAO itemDAO;

    @Autowired
    public ItemServiceImpl(ItemDAO theItemDAO) {
        itemDAO = theItemDAO;
    }

    @Override
    public List<Item> findAll() {
        return itemDAO.findAll();
    };

    @Override
    public Page<Item> findAll(Pageable pageable, List<String> classes) {

            Specification<Item> spec = (root, query, builder) -> {
                if (classes == null || classes.isEmpty()) {
                    return builder.conjunction();
                }
                return root.get("classItem").in(classes);
            };
            return itemDAO.findAll(spec, pageable);

    };

    @Override
    public void saveItem(Item item) { itemDAO.save(item); };

    @Override
    public void deleteBySku(String sku) { itemDAO.deleteById(sku); }

    @Override
    public void updateItem(Item item) {
        Optional<Item> searchItem = itemDAO.findById(item.getSku());

        if(searchItem.isPresent()) {
            Item updateItem = searchItem.get();
            updateItem.setName(item.getName());
            updateItem.setCraftable(item.isCraftable());
            updateItem.setClassItem(item.getClassItem());
            updateItem.setQuality(item.getQuality());
            updateItem.setType(item.getType());
            updateItem.setImage(item.getImage());
            itemDAO.save(updateItem);
        } else {
            System.out.println("Not Found");
        }
    }

}
