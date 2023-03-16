package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.components.ExcelGenerator;
import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.*;
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
        Page<Sale> sales;
        Pageable pageable = PageRequest.of(page, size);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        LocalDateTime start = null;
        if (startDate != null && !startDate.equals("")) {
            long timestamp = Long.parseLong(startDate);
            Date date = new Date(timestamp);
            String dateStartString = formatter.format(date);
            start = LocalDateTime.parse(dateStartString, formatterDate);
        }

        LocalDateTime end = null;
        if (endDate != null && !endDate.equals("")) {
            long timestamp = Long.parseLong(endDate);
            Date date = new Date(timestamp);
            String dateEndString = formatter.format(date);
            end = LocalDateTime.parse(dateEndString, formatterDate);
        }

        Double minimumPriceValue = null;

        if(minPrice != null && !minPrice.isEmpty())
            minimumPriceValue = Double.parseDouble(minPrice);


        Double maximumPriceValue = null;
        if(maxPrice != null && !maxPrice.isEmpty())
            maximumPriceValue = Double.parseDouble(maxPrice);


        User user = userService.getCurrentUser();

        if(types.contains("none"))
            types = Arrays.asList("");

        sales = saleService.findAll(pageable, user, craftable, classes, qualities, types, start, end, minimumPriceValue, maximumPriceValue);

        PagedModel<Sale> pagedModel = PagedModel.of(sales.getContent(), new PagedModel.PageMetadata(sales.getSize(), sales.getNumber(), sales.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @PostMapping("/fetchCSV")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, CsvException {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "No file attached" + "\" }");
        }

        String[] expectedHeader = {"name", "sku", "orderid", "date", "status", "price", "net", "fee"};

        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));

        List<String[]> records = csvReader.readAll();

        if (!Arrays.equals(records.get(0), expectedHeader)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "No necessary headers" + "\" }");
        }

        List<Sale> sales = new ArrayList<>();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ofPattern("d MMMM, yyyy H:mm"))
                .optionalStart()
                .appendLiteral(',')
                .optionalEnd()
                .optionalStart()
                .appendPattern(" a")
                .optionalEnd()
                .toFormatter(Locale.ENGLISH);

        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);

            Sale sale = new Sale();

            sale.setAssignSku(record[1]);
            sale.setOrderId(record[2]);
            sale.setDate( LocalDateTime.parse(record[3], formatter) );
            sale.setPrice(Double.parseDouble(record[5]));
            sale.setNet(Double.parseDouble(record[6]));
            sale.setFee(Double.parseDouble(record[7]));
            sale.setUser(userService.getCurrentUser());

            Item theItem = itemService.findItemBySku(record[1]);

            if(theItem == null)
                sale.setItem(null);
            else
                sale.setItem(theItem);

            System.out.println(sale.getAssignSku());

            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<Sale>> violations = validator.validate(sale);
            if (!violations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ \"error\": \"" + "Wrong file format data" + "\" }");
            }

            sales.add(sale);
        }

        try {
            saleService.saveAll(sales);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Data saved" + "\" }");

        } catch( Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Error occured" + "\" }");
        }
    }

    @GetMapping("graphs/show")
    public String openGraphs() {
        return "sales/graphs";
    }

    @GetMapping("graphs/get-years")
    public ResponseEntity<List<String>> getYears() {
        try {
            List<String> years = saleService.getYears(userService.getCurrentUser());
            return ResponseEntity.ok(years);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    static class SalesItemsDTO {

        String sku;
        String name;
        Long count;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public static List<SalesItemsDTO> getList(List<Object[]> results) {

            List<SalesItemsDTO> itemsByMonth = new ArrayList<>();

            for (Object[] row : results) {
                SalesItemsDTO dto = new SalesItemsDTO();
                dto.setSku((String) row[0]);
                dto.setName((String) row[1]);
                dto.setCount((Long) row[2]);
                itemsByMonth.add(dto);
            }

            return itemsByMonth;
        }
    }
    @GetMapping("graphs/sales-year")
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByMonthInYear(@RequestParam int year) {
        try {
            List<Object[]> results = saleService.getSalesCountByMonthInYear(year, userService.getCurrentUser());

            List<Map<String, Object>> salesByMonth = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("month", row[0]);
                map.put("count", row[1]);
                salesByMonth.add(map);
            }

            return ResponseEntity.ok(salesByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("graphs/sales-items-month")
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByMonthInYear(@RequestParam int page, @RequestParam int year, @RequestParam int month) {
        try {
            List<Object[]> results = saleService.getItemsDataFromMonth(year, month, page, 5, userService.getCurrentUser());
            List<SalesItemsDTO> itemsByMonth = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(itemsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("graphs/sales-items-month-total-pages")
    public ResponseEntity<Integer> getItemsMonthTotalPages(@RequestParam int year, @RequestParam int month) {
        try {
            Integer pages = saleService.getItemsDataFromMonthTotalPages(year,month, 5, userService.getCurrentUser());

            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("graphs/sales-items-day")
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByDayInMonth(@RequestParam int year, @RequestParam int month, @RequestParam int day, @RequestParam int page) {

        try {
            List<Object[]> results = saleService.getItemsDataFromDay(year,month,day,page, 5, userService.getCurrentUser());
            List<SalesItemsDTO> itemsByMonth = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(itemsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("graphs/sales-items-day-total-pages")
    public ResponseEntity<Integer> getItemsDayTotalPages(@RequestParam int year, @RequestParam int month, @RequestParam int day) {
        try {
            Integer pages = saleService.getItemsDataFromDayTotalPages(year, month, day, 5, userService.getCurrentUser());

            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("graphs/sales-month")
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByDayInMonth(@RequestParam int year, @RequestParam int month) {
        try {
            List<Object[]> results = saleService.getSalesCountByDayinMonth(year, month, userService.getCurrentUser());

            List<Map<String, Object>> salesByDay = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("day", row[0]);
                map.put("count", row[1]);
                salesByDay.add(map);
            }
            return ResponseEntity.ok(salesByDay);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("graphs/sales-best")
    public ResponseEntity<List<SalesItemsDTO>> getBestSales(@RequestParam int year, @RequestParam(required = false) Integer month) {
        try {
            List<Object[]> results = saleService.getBestOrWorstSellingItems(year, month, true, userService.getCurrentUser());
            List<SalesItemsDTO> bestSales = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(bestSales);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("graphs/sales-worst")
    public ResponseEntity<List<SalesItemsDTO>> getWorstSales(@RequestParam int year, @RequestParam(required = false) Integer month) {
        try {
            List<Object[]> results = saleService.getBestOrWorstSellingItems(year, month, false, userService.getCurrentUser());
            List<SalesItemsDTO> worstSales = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(worstSales);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("graphs/fetch-months")
    public ResponseEntity<List<Integer>> getMonthsByYear(@RequestParam int year) {
        try {
            List<Integer> results = saleService.getMonthsByYear(year, userService.getCurrentUser());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("excelSummary")
    public void downloadExcel(HttpServletResponse response, @RequestParam(defaultValue = "", required = false) String craftable,
                              @RequestParam(defaultValue = "", required = false) List<String> classes,
                              @RequestParam(defaultValue = "", required = false) List<String> qualities,
                              @RequestParam(defaultValue = "", required = false) List<String> types,
                              @RequestParam(defaultValue = "", required = false) String startDate,
                              @RequestParam(defaultValue = "", required = false) String endDate,
                              @RequestParam(defaultValue = "", required = false) String minPrice,
                              @RequestParam(defaultValue = "", required = false) String maxPrice) throws IOException {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        LocalDateTime start = null;
        if (startDate != null && !startDate.equals("")) {
            long timestamp = Long.parseLong(startDate);
            Date date = new Date(timestamp);
            String dateStartString = formatter.format(date);
            start = LocalDateTime.parse(dateStartString, formatterDate);
        }

        LocalDateTime end = null;
        if (endDate != null && !endDate.equals("")) {
            long timestamp = Long.parseLong(endDate);
            Date date = new Date(timestamp);
            String dateEndString = formatter.format(date);
            end = LocalDateTime.parse(dateEndString, formatterDate);
        }

        Double minimumPriceValue = null;

        if(minPrice != null && !minPrice.isEmpty())
            minimumPriceValue = Double.parseDouble(minPrice);

        Double maximumPriceValue = null;
        if(maxPrice != null && !maxPrice.isEmpty())
            maximumPriceValue = Double.parseDouble(maxPrice);

        if(types.contains("none"))
            types = Arrays.asList("");

        List<Sale> sales = saleService.findAll(userService.getCurrentUser(),craftable,classes,qualities,types,start,end,minimumPriceValue, maximumPriceValue);
        for(Sale sale: sales)
            System.out.println(sale.getDate());

        excelGenerator.generateExcelFile(sales, response);
    }
}
