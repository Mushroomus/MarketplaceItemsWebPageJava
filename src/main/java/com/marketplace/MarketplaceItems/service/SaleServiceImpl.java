package com.marketplace.MarketplaceItems.service;

import com.marketplace.MarketplaceItems.components.ExcelGenerator;
import com.marketplace.MarketplaceItems.dao.SaleDAO;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.model.SalesItemsDTO;
import com.marketplace.MarketplaceItems.service.Manager.ItemSale;
import com.marketplace.MarketplaceItems.service.Manager.MessageSale;
import com.marketplace.MarketplaceItems.service.Manager.SaleItem;
import com.marketplace.MarketplaceItems.service.Manager.SaleUser;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class SaleServiceImpl implements SaleService, MessageSale, ItemSale {

    private SaleDAO saleDAO;

    private SaleUser saleUser;

    private SaleItem saleItem;

    private ExcelGenerator excelGenerator;

    @Autowired
    public void setSaleDAO(SaleDAO theSaleDAO) {
        saleDAO = theSaleDAO;
    }

    @Autowired
    public void setSaleUser(SaleUser theSaleUser) {
        saleUser = theSaleUser;
    }

    @Autowired
    public void setSaleItem(SaleItem theSaleItem) {
        saleItem = theSaleItem;
    }

    @Autowired
    public void setExcelGenerator(ExcelGenerator theExcelGenerator) {
        excelGenerator = theExcelGenerator;
    }

    @Override
    public Map<String, Boolean> checkSales() {
        User user = saleUser.getCurrentUser();
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


        User user = saleUser.getCurrentUser();

        if(types.contains("none"))
            types = Arrays.asList("");

        sales = findAll(pageable, user, craftable, classes, qualities, types, start, end, minimumPriceValue, maximumPriceValue);

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
            sale.setUser(saleUser.getCurrentUser());

            Item theItem = saleItem.findItemBySku(record[1]);

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

    @Override
    public ResponseEntity<List<String>> getYears() {
        try {
            List<String> years = getYears(saleUser.getCurrentUser());
            return ResponseEntity.ok(years);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByMonthInYear(int year) {
        try {
            List<Object[]> results = getSalesCountByMonthInYear(year, saleUser.getCurrentUser());

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

    @Override
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByMonthInYear(int page, int year, int month) {
        try {
            List<Object[]> results = getItemsDataFromMonth(year, month, page, 5, saleUser.getCurrentUser());
            List<SalesItemsDTO> itemsByMonth = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(itemsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Integer> getItemsMonthTotalPages(@RequestParam int year, @RequestParam int month) {
        try {
            Integer pages = getItemsDataFromMonthTotalPages(year,month, 5, saleUser.getCurrentUser());
            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<SalesItemsDTO>> getSalesCountByDayInMonth(int year, int month, int day, int page) {
        try {
            List<Object[]> results = getItemsDataFromDay(year,month,day,page, 5, saleUser.getCurrentUser());
            List<SalesItemsDTO> itemsByMonth = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(itemsByMonth);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Integer> getItemsDayTotalPages(int year, int month, int day) {
        try {
            Integer pages = getItemsDataFromDayTotalPages(year, month, day, 5, saleUser.getCurrentUser());

            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getSalesCountByDayInMonth(int year, int month) {
        try {
            List<Object[]> results = getSalesCountByDayinMonth(year, month, saleUser.getCurrentUser());

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


    @Override
    public ResponseEntity<List<SalesItemsDTO>> getBestSales(int year, Integer month) {
        try {
            List<Object[]> results = getBestOrWorstSellingItems(year, month, true, saleUser.getCurrentUser());
            List<SalesItemsDTO> bestSales = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(bestSales);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<SalesItemsDTO>> getWorstSales(int year, Integer month) {
        try {
            List<Object[]> results = getBestOrWorstSellingItems(year, month, false, saleUser.getCurrentUser());
            List<SalesItemsDTO> worstSales = SalesItemsDTO.getList(results);

            return ResponseEntity.ok(worstSales);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<Integer>> getMonthsByYear(int year) {
        try {
            List<Integer> results = getMonthsByYear(year, saleUser.getCurrentUser());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

        List<Sale> sales = findAll(saleUser.getCurrentUser(),craftable,classes,qualities,types,start,end,minimumPriceValue, maximumPriceValue);
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

    private Page<Sale> findAll(Pageable pageable, User user, String craftable, List<String> classes, List<String> qualities, List<String> types, LocalDateTime startDate, LocalDateTime endDate, Double minPrice, Double maxPrice) {
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
