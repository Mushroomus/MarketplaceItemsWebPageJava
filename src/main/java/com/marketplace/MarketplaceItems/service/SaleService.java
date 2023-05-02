package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.model.SalesItemsDTO;
import com.opencsv.exceptions.CsvException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface SaleService {
    Map<String, Boolean> checkSales();
    ResponseEntity<PagedModel<Sale>> getSales(int page, int size, String craftable, List<String> classes, List<String> qualities, List<String> types, String startDate, String endDate, String minPrice, String maxPrice);
    ResponseEntity<String> handleFileUpload(MultipartFile file) throws IOException, CsvException;
    ResponseEntity<List<String>> getYears();
    ResponseEntity<List<Map<String, Object>>> getSalesCountByMonthInYear(int year);
    ResponseEntity<List<SalesItemsDTO>> getSalesCountByMonthInYear(int page, int year, int month);
    ResponseEntity<Integer> getItemsMonthTotalPages(@RequestParam int year, @RequestParam int month);
    ResponseEntity<List<SalesItemsDTO>> getSalesCountByDayInMonth(int year, int month, int day, int page);
    ResponseEntity<Integer> getItemsDayTotalPages(int year, int month, int day);
    ResponseEntity<List<Map<String, Object>>> getSalesCountByDayInMonth(int year, int month);
    ResponseEntity<List<SalesItemsDTO>> getBestSales(int year, Integer month);
    ResponseEntity<List<SalesItemsDTO>> getWorstSales(int year, Integer month);
    ResponseEntity<List<Integer>> getMonthsByYear(int year);
    void downloadExcel(HttpServletResponse response, String craftable, List<String> classes, List<String> qualities, List<String> types, String startDate, String endDate,
                  String minPrice, String maxPrice);
}

