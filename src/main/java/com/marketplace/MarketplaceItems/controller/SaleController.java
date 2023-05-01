package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.components.ExcelGenerator;
import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.model.SalesItemsDTO;
import com.marketplace.MarketplaceItems.service.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.*;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.poi.ss.usermodel.*;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private SaleService saleService;

    private UserService userService;

    private ItemService itemService;

    @Autowired
    private ExcelGenerator excelGenerator;

    public SaleController(SaleService theSaleService, UserService theUserService, ItemService theItemService) {
        saleService = theSaleService;
        userService = theUserService;
        itemService = theItemService;
    }

    @GetMapping("/graphs/check-sales")
    @ResponseBody
    public Map<String, Boolean> checkSales() {
        return saleService.checkSales();
    }

    @GetMapping("/list")
    public String showSales(Model theModel) {
        List<String> classesList = Arrays.asList("Multi-class", "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        List<String> qualityList = Arrays.asList("Genuine", "Vintage", "Unique", "Strange", "Haunted");
        List<String> typeList = Arrays.asList("Cosmetics", "Currencies", "Tools", "Paints", "Action", "Weapons", "Strange Parts", "Botkillers", "Festive Weapons", "Halloween");

        theModel.addAttribute("craftableOptions", Arrays.asList("Any", "Yes", "No"));
        theModel.addAttribute("classesList", classesList);
        theModel.addAttribute("qualityList", qualityList);
        theModel.addAttribute("typeList", typeList);
        return "sales/show-sales";
    }

    @GetMapping("/list-refresh")
    public ResponseEntity<PagedModel<Sale>> refreshList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "", required = false) String craftable,
                                                        @RequestParam(defaultValue = "", required = false) List<String> classes,
                                                        @RequestParam(defaultValue = "", required = false) List<String> qualities,
                                                        @RequestParam(defaultValue = "", required = false) List<String> types,
                                                        @RequestParam(defaultValue = "", required = false) String startDate,
                                                        @RequestParam(defaultValue = "", required = false) String endDate,
                                                        @RequestParam(defaultValue = "", required = false) String minPrice,
                                                        @RequestParam(defaultValue = "", required = false) String maxPrice) {

        return saleService.getSales(page, size, craftable, classes, qualities, types, startDate, endDate, minPrice, maxPrice);
    }

    @PostMapping("/fetchCSV")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            return saleService.handleFileUpload(file);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Something wen wrong" + "\" }");
        }
    }

    @GetMapping("graphs/show")
    public String openGraphs() {
        return "sales/graphs";
    }

    @GetMapping("graphs/get-years")
    public ResponseEntity<List<String>> getYears() {
        return saleService.getYears();
    }


    @GetMapping("graphs/sales-year")
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByMonthInYear(@RequestParam int year) {
        return saleService.getSalesCountByMonthInYear(year);
    }

    @GetMapping("graphs/sales-items-month")
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByMonthInYear(@RequestParam int page, @RequestParam int year, @RequestParam int month) {
        return saleService.getSalesCountByMonthInYear(page, year, month);
    }

    @GetMapping("graphs/sales-items-month-total-pages")
    public ResponseEntity<Integer> getItemsMonthTotalPages(@RequestParam int year, @RequestParam int month) {
        return saleService.getItemsMonthTotalPages(year, month);
    }

    @GetMapping("graphs/sales-items-day")
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByDayInMonth(@RequestParam int year, @RequestParam int month, @RequestParam int day, @RequestParam int page) {
        return saleService.getSalesCountByDayInMonth(year, month, day, page);
    }

    @GetMapping("graphs/sales-items-day-total-pages")
    public ResponseEntity<Integer> getItemsDayTotalPages(@RequestParam int year, @RequestParam int month, @RequestParam int day) {
        return saleService.getItemsDayTotalPages(year, month, day);
    }



    @GetMapping("graphs/sales-month")
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByDayInMonth(@RequestParam int year, @RequestParam int month) {
        return saleService.getSalesCountByDayInMonth(year, month);
    }

    @GetMapping("graphs/sales-best")
    public ResponseEntity<List<SalesItemsDTO>> getBestSales(@RequestParam int year, @RequestParam(required = false) Integer month) {
        return saleService.getBestSales(year, month);
    }

    @GetMapping("graphs/sales-worst")
    public ResponseEntity<List<SalesItemsDTO>> getWorstSales(@RequestParam int year, @RequestParam(required = false) Integer month) {
        return saleService.getWorstSales(year, month);
    }

    @GetMapping("graphs/fetch-months")
    public ResponseEntity<List<Integer>> getMonthsByYear(@RequestParam int year) {
        return saleService.getMonthsByYear(year);
    }

    @GetMapping("excelSummary")
    public void downloadExcel(HttpServletResponse response, @RequestParam(defaultValue = "", required = false) String craftable,
                              @RequestParam(defaultValue = "", required = false) List<String> classes,
                              @RequestParam(defaultValue = "", required = false) List<String> qualities,
                              @RequestParam(defaultValue = "", required = false) List<String> types,
                              @RequestParam(defaultValue = "", required = false) String startDate,
                              @RequestParam(defaultValue = "", required = false) String endDate,
                              @RequestParam(defaultValue = "", required = false) String minPrice,
                              @RequestParam(defaultValue = "", required = false) String maxPrice) {
        saleService.downloadExcel(response, craftable, classes, qualities, types, startDate, endDate, minPrice, maxPrice);
    }
}
