package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.SaleDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


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
    public List<Object[]> getSalesCountByDayinMonth(int year, int month) {
        String hql = "SELECT DAY(s.date), COUNT(s.id) FROM Sale s WHERE YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY DAY(s.date) ORDER BY DAY(s.date)";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getItemsDataFromMonth(int year, int month, int page, int pageSize) {
        String hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY i.sku, i.name ORDER BY COUNT(s.id) DESC";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public int getItemsDataFromMonthTotalPages(int year, int month, int pageSize) {
        String countHql = "SELECT COUNT(DISTINCT i.sku) FROM Sale s INNER JOIN s.item i WHERE YEAR(s.date) = :year AND MONTH(s.date) = :month";
        TypedQuery<Long> countQuery = entityManager.createQuery(countHql, Long.class);
        countQuery.setParameter("year", year);
        countQuery.setParameter("month", month);

        Long totalItems = countQuery.getSingleResult();
        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        return totalPages;
    }

    @Override
    public List<Object[]> getItemsDataFromDay(int year, int month, int day, int page, int pageSize) {
        String hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE YEAR(s.date) = :year AND MONTH(s.date) = :month AND DAY(s.date) = :day GROUP BY i.sku, i.name ORDER BY COUNT(s.id) DESC";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("day", day);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public int getItemsDataFromDayTotalPages(int year, int month, int day, int pageSize) {
        String countHql = "SELECT COUNT(DISTINCT i.sku) FROM Sale s INNER JOIN s.item i WHERE YEAR(s.date) = :year AND MONTH(s.date) = :month AND DAY(s.date) = :day";
        TypedQuery<Long> countQuery = entityManager.createQuery(countHql, Long.class);
        countQuery.setParameter("year", year);
        countQuery.setParameter("month", month);
        countQuery.setParameter("day", day);

        Long totalItems = countQuery.getSingleResult();
        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        return totalPages;
    }

    @Override
    public List<Object[]> getBestOrWorstSellingItems(int year, Integer month, boolean best) {

        String hql;

        if(month == null)
            hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE YEAR(s.date) = :year GROUP BY i.sku, i.name";
        else
            hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY i.sku, i.name";

        if (best) {
            hql += " ORDER BY COUNT(s.id) DESC";
        } else {
            hql += " ORDER BY COUNT(s.id) ASC";
        }

        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        if(month != null)
            query.setParameter("month", month);

        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public List<Integer> getMonthsByYear(@RequestParam int year) {
        String hql = "SELECT DISTINCT MONTH(s.date) FROM Sale s WHERE YEAR(s.date) = :year ORDER BY MONTH(s.date) ASC";
        TypedQuery<Integer> query = entityManager.createQuery(hql, Integer.class);
        query.setParameter("year", year);

        return query.getResultList();

    }

}
