package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.dao.SaleDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.Manager.MessageSale;
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
public class SaleServiceImpl implements SaleService, MessageSale {

    private SaleDAO saleDAO;

    @Autowired
    public SaleServiceImpl(SaleDAO theSaleDAO) {
        saleDAO = theSaleDAO;
    }

    @Override
    public boolean userHasSales(User user) {
        List<Sale> sales = saleDAO.findByUser(user);

        if(sales.isEmpty())
            return false;
        return true;
    }

    @Override
    public Page<Sale> findAll(Pageable pageable) {
        return saleDAO.findAll(pageable);
    }

    @Override
    public void saveAll(List<Sale> sales) {
        saleDAO.saveAll(sales);
    }

    @Override
    public void updateItemDeletedNull(Item item) {
        saleDAO.updateItemDeletedNull(item);
    }

    @Override
    public void updateSkuNewAddedItem(Item item, String sku) { saleDAO.updateSkuNewAddedItem(item, sku); }

    @Override
    public void deleteAllByUserId(int user_id) {
        saleDAO.deleteAllByUserId(user_id);
    }

    @PersistenceContext
    private EntityManager entityManager;

    public long countSalesByFilters(String craftable, User user, List<String> classes, List<String> qualities, List<String> types, LocalDateTime startDate,
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

        predicates.add(builder.equal(root.get("user"), user));

        query.select(builder.count(root)).where(predicates.toArray(new Predicate[predicates.size()]));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public Page<Sale> findAll(Pageable pageable, User user, String craftable, List<String> classes, List<String> qualities, List<String> types, LocalDateTime startDate, LocalDateTime endDate, Double minPrice, Double maxPrice) {
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

        predicates.add(builder.equal(root.get("user"), user));

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Sale> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Sale> sales = typedQuery.getResultList();
        return new PageImpl<>(sales, pageable, countSalesByFilters(craftable, user, classes, qualities, types, startDate, endDate, minPrice, maxPrice));
        }

    @Override
    public List<Sale> findAll(User user, String craftable, List<String> classes, List<String> qualities, List<String> types, LocalDateTime startDate, LocalDateTime endDate, Double minPrice, Double maxPrice) {
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

        predicates.add(builder.equal(root.get("user"), user));

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Sale> typedQuery = entityManager.createQuery(query);
        List<Sale> sales = typedQuery.getResultList();

        return sales;
    }

    @Override
    public List<String> getYears(User user) {
        return saleDAO.getDistinctYears(user);
    }


    @Override
    public List<Object[]> getSalesCountByMonthInYear(int year, User user) {
        String hql = "SELECT MONTH(s.date), COUNT(s.id) FROM Sale s WHERE YEAR(s.date) = :year AND s.user = :user GROUP BY MONTH(s.date)";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getSalesCountByDayinMonth(int year, int month, User user) {
        String hql = "SELECT DAY(s.date), COUNT(s.id) FROM Sale s WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY DAY(s.date) ORDER BY DAY(s.date)";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getItemsDataFromMonth(int year, int month, int page, int pageSize, User user) {
        String hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY i.sku, i.name ORDER BY COUNT(s.id) DESC";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("user", user);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public int getItemsDataFromMonthTotalPages(int year, int month, int pageSize, User user) {
        String countHql = "SELECT COUNT(DISTINCT i.sku) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month";
        TypedQuery<Long> countQuery = entityManager.createQuery(countHql, Long.class);
        countQuery.setParameter("year", year);
        countQuery.setParameter("month", month);
        countQuery.setParameter("user", user);

        Long totalItems = countQuery.getSingleResult();
        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        return totalPages;
    }

    @Override
    public List<Object[]> getItemsDataFromDay(int year, int month, int day, int page, int pageSize, User user) {
        String hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month AND DAY(s.date) = :day GROUP BY i.sku, i.name ORDER BY COUNT(s.id) DESC";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("day", day);
        query.setParameter("user", user);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public int getItemsDataFromDayTotalPages(int year, int month, int day, int pageSize, User user) {
        String countHql = "SELECT COUNT(DISTINCT i.sku) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month AND DAY(s.date) = :day";
        TypedQuery<Long> countQuery = entityManager.createQuery(countHql, Long.class);
        countQuery.setParameter("year", year);
        countQuery.setParameter("month", month);
        countQuery.setParameter("day", day);
        countQuery.setParameter("user", user);

        Long totalItems = countQuery.getSingleResult();
        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        return totalPages;
    }

    @Override
    public List<Object[]> getBestOrWorstSellingItems(int year, Integer month, boolean best, User user) {

        String hql;

        if(month == null)
            hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year GROUP BY i.sku, i.name";
        else
            hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY i.sku, i.name";

        if (best) {
            hql += " ORDER BY COUNT(s.id) DESC";
        } else {
            hql += " ORDER BY COUNT(s.id) ASC";
        }

        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("user", user);

        if(month != null)
            query.setParameter("month", month);

        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public List<Integer> getMonthsByYear(int year, User user) {
        String hql = "SELECT DISTINCT MONTH(s.date) FROM Sale s WHERE s.user = :user AND YEAR(s.date) = :year ORDER BY MONTH(s.date) ASC";
        TypedQuery<Integer> query = entityManager.createQuery(hql, Integer.class);
        query.setParameter("year", year);
        query.setParameter("user", user);

        return query.getResultList();
    }

}
