package com.marketplace.MarketplaceItems.service.implementation;

import com.marketplace.MarketplaceItems.components.ExcelGenerator;
import com.marketplace.MarketplaceItems.dao.SaleDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.exception.InternalServerErrorException;
import com.marketplace.MarketplaceItems.exception.RecordNotFoundException;
import com.marketplace.MarketplaceItems.model.SalesItemsDTO;
import com.marketplace.MarketplaceItems.service.SaleService;
import com.marketplace.MarketplaceItems.service.operation.ItemSaleOperations;
import com.marketplace.MarketplaceItems.service.operation.MessageSaleOperations;
import com.marketplace.MarketplaceItems.service.operation.SaleItemOperations;
import com.marketplace.MarketplaceItems.service.operation.SaleUserOperations;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@Service
public class SaleServiceImpl implements SaleService, MessageSaleOperations, ItemSaleOperations {

    private SaleDAO saleDAO;

    private SaleUserOperations saleUserOperations;

    private SaleItemOperations saleItemOperations;

    private ExcelGenerator excelGenerator;

    @Autowired
    public void setSaleDAO(SaleDAO theSaleDAO) {
        saleDAO = theSaleDAO;
    }

    @Autowired
    public void setSaleUser(@Qualifier("userServiceImpl") SaleUserOperations theSaleUserOperations) {
        saleUserOperations = theSaleUserOperations;
    }

    @Autowired
    public void setSaleItem(@Qualifier("itemServiceImpl") SaleItemOperations theSaleItemOperations) {
        saleItemOperations = theSaleItemOperations;
    }

    @Autowired
    public void setExcelGenerator(ExcelGenerator theExcelGenerator) {
        excelGenerator = theExcelGenerator;
    }

    @Override
    public Map<String, Boolean> checkSales() {
        User user = saleUserOperations.getCurrentUser();
        List<Sale> sales = saleDAO.findByUser(user);

        boolean salesEmpty = true;

        if(sales.isEmpty())
            salesEmpty = false;

        Map<String, Boolean> result = new HashMap<>();
        result.put("salesEmpty", salesEmpty);

        return result;
    }

    @Override
    public ResponseEntity<PagedModel<Sale>> getSales(int page, int size, String craftable, List<String> classes, List<String> qualities, List<String> types, String startDate, String endDate, String minPrice, String maxPrice) {
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

        User user = saleUserOperations.getCurrentUser();
        classes = classes.isEmpty() ? null : classes;
        qualities = qualities.isEmpty() ? null : qualities;
        types = types.isEmpty() ? null : types;

        Boolean craftableCheck = "Yes".equals(craftable) ? Boolean.TRUE : "No".equals(craftable) ? Boolean.FALSE : null;

        sales = new PageImpl<>(saleDAO.getSalesPagination(user, craftableCheck, classes, qualities, types, start, end, minimumPriceValue, maximumPriceValue, pageable).toList(), pageable,
                saleDAO.countSalesByFilters(user, craftableCheck, classes, qualities, types, start, end, minimumPriceValue, maximumPriceValue));

        PagedModel<Sale> pagedModel = PagedModel.of(sales.getContent(), new PagedModel.PageMetadata(sales.getSize(), sales.getNumber(), sales.getTotalElements()));

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> handleFileUpload(MultipartFile file) throws IOException, CsvException {

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
            sale.setUser(saleUserOperations.getCurrentUser());

            Item theItem = saleItemOperations.findItemBySku(record[1]);

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
            saleDAO.saveAll(sales);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Data saved" + "\" }");

        } catch( Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{ \"error\": \"" + "Error occured" + "\" }");
        }
    }

    private User getUser() {
        User user = saleUserOperations.getCurrentUser();
        if (user == null) {
            throw new RecordNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public ResponseEntity<List<String>> getYears() {
        try {
            List<String> years = getYears(getUser());
            return ResponseEntity.status(HttpStatus.OK).body(years);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting years");
        }
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByMonthInYear(int year) {
        try {
            List<Object[]> results = getSalesCountByMonthInYear(year, getUser());

            List<Map<String, Object>> salesByMonth = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("month", row[0]);
                map.put("count", row[1]);
                salesByMonth.add(map);
            }
            return ResponseEntity.status(HttpStatus.OK).body(salesByMonth);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting sales count by month in year");
        }
    }

    @Override
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByMonthInYear(int page, int year, int month) {
        try {
            List<Object[]> results = getItemsDataFromMonth(year, month, page, 5, getUser());
            List<SalesItemsDTO> itemsByMonth = SalesItemsDTO.getList(results);
            return ResponseEntity.status(HttpStatus.OK).body(itemsByMonth);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting sales count by month in year");
        }
    }

    @Override
    public ResponseEntity<Integer> getItemsMonthTotalPages(@RequestParam int year, @RequestParam int month) {
        try {
            Integer pages = getItemsDataFromMonthTotalPages(year,month, 5, getUser());
            return ResponseEntity.status(HttpStatus.OK).body(pages);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting items month total pages");
        }
    }

    @Override
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByDayInMonth(int year, int month, int day, int page) {
        try {
            List<Object[]> results = getItemsDataFromDay(year,month,day,page, 5, getUser());
            List<SalesItemsDTO> itemsByMonth = SalesItemsDTO.getList(results);
            return ResponseEntity.status(HttpStatus.OK).body(itemsByMonth);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting sales count by day in month");
        }
    }

    @Override
    public ResponseEntity<Integer> getItemsDayTotalPages(int year, int month, int day) {
        try {
            Integer pages = getItemsDataFromDayTotalPages(year, month, day, 5, getUser());
            return ResponseEntity.status(HttpStatus.OK).body(pages);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting items day total pages");
        }
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByDayInMonth(int year, int month) {
        try {
            List<Object[]> results = getSalesCountByDayinMonth(year, month, getUser());

            List<Map<String, Object>> salesByDay = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("day", row[0]);
                map.put("count", row[1]);
                salesByDay.add(map);
            }
            return ResponseEntity.status(HttpStatus.OK).body(salesByDay);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting sales count by day in month");
        }
    }


    @Override
    public ResponseEntity<List<SalesItemsDTO>> getBestSales(int year, Integer month) {
        try {
            List<Object[]> results = getBestOrWorstSellingItems(year, month, true, getUser());
            List<SalesItemsDTO> bestSales = SalesItemsDTO.getList(results);
            return ResponseEntity.status(HttpStatus.OK).body(bestSales);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting best sales");
        }
    }

    @Override
    public ResponseEntity<List<SalesItemsDTO>> getWorstSales(int year, Integer month) {
        try {
            List<Object[]> results = getBestOrWorstSellingItems(year, month, false, getUser());
            List<SalesItemsDTO> worstSales = SalesItemsDTO.getList(results);
            return ResponseEntity.status(HttpStatus.OK).body(worstSales);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting worst sales");
        }
    }

    @Override
    public ResponseEntity<List<Integer>> getMonthsByYear(int year) {
        try {
            List<Integer> results = getMonthsByYear(year, getUser());
            return ResponseEntity.status(HttpStatus.OK).body(results);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error occurred while getting months by year");
        }
    }

    @Override
    public void downloadExcel(HttpServletResponse response, String craftable, List<String> classes, List<String> qualities, List<String> types, String startDate, String endDate,
                              String minPrice, String maxPrice) {

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

        Boolean craftableCheck = null;
        if(craftable.equals("Yes"))
            craftableCheck = true;
        else if(craftable.equals("No"))
            craftableCheck = false;

        List<Sale> sales = saleDAO.getSalesNoPagination(saleUserOperations.getCurrentUser(),craftableCheck,classes,qualities,types,start,end,minimumPriceValue, maximumPriceValue);
        try {
            excelGenerator.generateExcelFile(sales, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateItemDeletedNull(Item item) {
        saleDAO.updateItemDeletedNull(item);
    }

    @Override
    public void updateSkuNewAddedItem(Item item, String sku) { saleDAO.updateSkuNewAddedItem(item, sku); }

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> getYears(User user) {
        return saleDAO.getDistinctYears(user);
    }

    private List<Object[]> getSalesCountByMonthInYear(int year, User user) {
        String hql = "SELECT MONTH(s.date), COUNT(s.id) FROM Sale s WHERE YEAR(s.date) = :year AND s.user = :user GROUP BY MONTH(s.date)";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("user", user);
        return query.getResultList();
    }

    private List<Object[]> getSalesCountByDayinMonth(int year, int month, User user) {
        String hql = "SELECT DAY(s.date), COUNT(s.id) FROM Sale s WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY DAY(s.date) ORDER BY DAY(s.date)";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("user", user);
        return query.getResultList();
    }

    private List<Object[]> getItemsDataFromMonth(int year, int month, int page, int pageSize, User user) {
        String hql = "SELECT i.sku, i.name, COUNT(s.id) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month GROUP BY i.sku, i.name ORDER BY COUNT(s.id) DESC";
        TypedQuery<Object[]> query = entityManager.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("user", user);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    private int getItemsDataFromMonthTotalPages(int year, int month, int pageSize, User user) {
        String countHql = "SELECT COUNT(DISTINCT i.sku) FROM Sale s INNER JOIN s.item i WHERE s.user = :user AND YEAR(s.date) = :year AND MONTH(s.date) = :month";
        TypedQuery<Long> countQuery = entityManager.createQuery(countHql, Long.class);
        countQuery.setParameter("year", year);
        countQuery.setParameter("month", month);
        countQuery.setParameter("user", user);

        Long totalItems = countQuery.getSingleResult();
        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        return totalPages;
    }

    private List<Object[]> getItemsDataFromDay(int year, int month, int day, int page, int pageSize, User user) {
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

    private int getItemsDataFromDayTotalPages(int year, int month, int day, int pageSize, User user) {
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

    private List<Object[]> getBestOrWorstSellingItems(int year, Integer month, boolean best, User user) {

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

    private List<Integer> getMonthsByYear(int year, User user) {
        String hql = "SELECT DISTINCT MONTH(s.date) FROM Sale s WHERE s.user = :user AND YEAR(s.date) = :year ORDER BY MONTH(s.date) ASC";
        TypedQuery<Integer> query = entityManager.createQuery(hql, Integer.class);
        query.setParameter("year", year);
        query.setParameter("user", user);

        return query.getResultList();
    }

}
