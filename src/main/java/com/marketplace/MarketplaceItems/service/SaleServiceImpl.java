package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.SaleDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleServiceImpl implements  SaleService {

    private SaleDAO saleDAO;

    @Autowired
    public SaleServiceImpl(SaleDAO theSaleDAO) {
        saleDAO = theSaleDAO;
    }

    @Override
    public Page<Sale> findAll(Pageable pageable) {
        return saleDAO.findAll(pageable);
    }

    @Override
    public void saveAll(List<Sale> sales) {
        saleDAO.saveAll(sales);
    }

    @PersistenceContext
    private EntityManager entityManager;

    public long countSalesByFilters(String craftable, List<String> classes, List<String> qualities, List<String> types, LocalDateTime startDate,
                                    LocalDateTime endDate, Double minPrice, Double maxPrice) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Sale> root = query.from(Sale.class);
        List<Predicate> predicates = new ArrayList<>();

        // Filter by Item properties
        Join<Sale, Item> itemJoin = root.join("item", JoinType.LEFT);

        if (itemJoin != null) {
            if (craftable != null && !craftable.isEmpty()) {
                boolean craftableValue = craftable.equals("Yes") ? true : false;
                predicates.add(builder.in(itemJoin.get("craftable")).value(craftableValue));
            }
            if (classes != null && !classes.isEmpty()) {
                predicates.add(builder.in(itemJoin.get("classItem")).value(classes));
            }
            if (qualities != null && !qualities.isEmpty()) {
                predicates.add(builder.in(itemJoin.get("quality")).value(qualities));
            }
            if (types != null && !types.isEmpty()) {
                predicates.add(builder.in(itemJoin.get("type")).value(types));
            }
        }

        // Filter by Sale properties
        if (startDate != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
        }
        if (endDate != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("date"), endDate));
        }
        if (minPrice != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        query.select(builder.count(root)).where(predicates.toArray(new Predicate[predicates.size()]));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public Page<Sale> findAll(Pageable pageable, String craftable, List<String> classes, List<String> qualities, List<String> types, LocalDateTime startDate, LocalDateTime endDate, Double minPrice, Double maxPrice) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Sale> query = builder.createQuery(Sale.class);
        Root<Sale> root = query.from(Sale.class);
        List<Predicate> predicates = new ArrayList<>();

        // Filter by Item properties
        Join<Sale, Item> itemJoin = root.join("item", JoinType.LEFT);

        if (itemJoin != null) {
            if (craftable != null && !craftable.isEmpty()) {
                boolean craftableValue = craftable.equals("Yes") ? true : false;
                predicates.add(builder.in(itemJoin.get("craftable")).value(craftableValue));
            }
            if (classes != null && !classes.isEmpty()) {
                predicates.add(builder.in(itemJoin.get("classItem")).value(classes));
            }
            if (qualities != null && !qualities.isEmpty()) {
                predicates.add(builder.in(itemJoin.get("quality")).value(qualities));
            }
            if (types != null && !types.isEmpty()) {
                predicates.add(builder.in(itemJoin.get("type")).value(types));
            }
        }

        // Filter by Sale properties
        if (startDate != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
        }
        if (endDate != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("date"), endDate));
        }
        if (minPrice != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Sale> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Sale> sales = typedQuery.getResultList();
        return new PageImpl<>(sales, pageable, countSalesByFilters(craftable, classes, qualities, types, startDate, endDate, minPrice, maxPrice));
        }

    @Override
    public List<String> getYears() {
        return saleDAO.getDistinctYears();
    }

    @Override
    public List<Object[]> getSalesCountByMonthInYear(int year) {
        String hql = "SELECT MONTH(s.date), COUNT(s.id) FROM Sale s WHERE YEAR(s.date) = :year GROUP BY MONTH(s.date)";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getBestSellingItems() {
        String hql = "SELECT i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i GROUP BY i.name ORDER BY COUNT(s.id) DESC";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        System.out.println(query.getResultList());
        query.setMaxResults(5);
        System.out.println(query.getResultList());
        return query.getResultList();
    }

    @Override
    public List<Object[]> getWorstSellingItems() {
        String hql = "SELECT i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i GROUP BY i.name ORDER BY COUNT(s.id) ASC";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        System.out.println(query.getResultList());
        query.setMaxResults(5);
        System.out.println(query.getResultList());
        return query.getResultList();
    }

}
