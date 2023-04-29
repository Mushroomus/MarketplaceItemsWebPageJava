package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.ItemDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.service.Manager.MessageItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.persistence.criteria.Expression;

@Service
public class ItemServiceImpl implements ItemService, MessageItem {

    private ItemDAO itemDAO;

    @Autowired
    public ItemServiceImpl(ItemDAO theItemDAO) {
        itemDAO = theItemDAO;
    }

    public Page<Item> findAll(Pageable page) { return itemDAO.findAll(page); };

    public Page<Item> findAllFilters(Pageable pageable, String search, String craftable, List<String> classes, List<String> qualities, List<String> types) {

        Specification<Item> spec = (root, query, builder) -> {
            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if(!search.equals("")){
                String searchPattern = "%" + search + "%";
                predicates.add(builder.like(root.get("name"), searchPattern ));
            }
            if(!craftable.equals("")) {
                boolean craftableValue = craftable.equals("Yes") ? true : false;
                predicates.add(builder.equal(root.get("craftable"), craftableValue ));
            }
            if (classes.size() > 0) {
                predicates.add(builder.in(root.get("classItem")).value(classes));
            }
            if (qualities.size() > 0) {
                predicates.add(builder.in(root.get("quality")).value(qualities));
            }
            if (types.size() > 0) {
                predicates.add(builder.in(root.get("type")).value(types));
            }

            return predicates.isEmpty() ? builder.conjunction() : builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
        };

        return itemDAO.findAll(spec, pageable);

    }

    @Override
    public Page<Item> findAll(Pageable pageable, String craftable, List<String> classes, List<String> qualities, List<String> types) {

        Specification<Item> spec = (root, query, builder) -> {
            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if(craftable != null && !craftable.equals("Any")) {
                boolean craftableValue = craftable.equals("Yes") ? true : false;
                predicates.add(builder.equal(root.get("craftable"), craftableValue ));
            }
            if (classes != null && !classes.isEmpty()) {
                predicates.add(builder.in(root.get("classItem")).value(classes));
            }
            if (qualities != null && !qualities.isEmpty()) {
                predicates.add(builder.in(root.get("quality")).value(qualities));
            }
            if (types != null && !types.isEmpty()) {
                predicates.add(builder.in(root.get("type")).value(types));
            }
            return predicates.isEmpty() ? builder.conjunction() : builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
        };

        return itemDAO.findAll(spec, pageable);

    };

    @Override
    public Page<Item> findAll(Pageable pageable, String search) {
        String searchPattern = "%" + search + "%";
        return itemDAO.findByNameLikeIgnoreCaseOrSkuLikeIgnoreCase(searchPattern, searchPattern, pageable);
    }

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

            if(!(item.getImage() == null || item.getImage().isEmpty()))
                updateItem.setImage(item.getImage());

            itemDAO.save(updateItem);
        } else {
            System.out.println("Not Found");
        }
    }

    @Override
    public Item findItemBySku(String sku) { return itemDAO.findItemBySku(sku); }

    @Override
    public void updateMarketplacePriceBySku(String sku, Double marketplacePrice) {
        itemDAO.updateMarketplacePriceBySku(sku, marketplacePrice);
    }

}
