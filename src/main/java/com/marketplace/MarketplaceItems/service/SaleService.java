package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface SaleService {
    Page<Sale> findAll(Pageable pageable);

    void saveAll(List<Sale> sales);

    Page<Sale> findAll(Pageable pageable, String craftable, List<String> classes, List<String> qualities, List<String> types,
                       LocalDateTime startDate, LocalDateTime endDate, Double minPrice, Double maxPrice);

    List<String> getYears();


    List<Object[]> getSalesCountByMonthInYear(int year);

    List<Object[]> getSalesCountByDayinMonth(int year, int month);

    List<Object[]> getItemsDataFromMonth(int year, int month);

    List<Object[]> getBestSellingItems();

    List<Object[]> getWorstSellingItems();
}
