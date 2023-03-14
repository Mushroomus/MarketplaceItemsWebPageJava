package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface SaleService {
    Page<Sale> findAll(Pageable pageable);

    void saveAll(List<Sale> sales);

    void setItemNull(Item item);

    void setAddItem(Item item, String sku);

    void deleteAllByUserId(int user_id);

    Page<Sale> findAll(Pageable pageable, User user, String craftable, List<String> classes, List<String> qualities, List<String> types,
                       LocalDateTime startDate, LocalDateTime endDate, Double minPrice, Double maxPrice);

    List<String> getYears(User user);


    List<Object[]> getSalesCountByMonthInYear(int year, User user);

    List<Object[]> getSalesCountByDayinMonth(int year, int month, User user);

    List<Object[]> getItemsDataFromMonth(int year, int month, int page, int pageSize, User user);
    public int getItemsDataFromMonthTotalPages(int year, int month, int pageSize, User user);

    List<Object[]> getItemsDataFromDay(int year, int month, int day, int page, int pageSize, User user);

    int getItemsDataFromDayTotalPages(int year, int month, int day, int pageSize, User user);

    List<Object[]> getBestOrWorstSellingItems(int year, Integer month, boolean best, User user);


    List<Integer> getMonthsByYear(int year, User user);
}
