package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.entity.User;
import com.marketplace.MarketplaceItems.service.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
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

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private SaleService saleService;

    private UserService userService;

    private ItemService itemService;

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

        System.out.println(minPrice + " " + maxPrice);

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


        System.out.println(minPrice);
        Double minimumPriceValue = null;

        if(minPrice != null && !minPrice.isEmpty())
            minimumPriceValue = Double.parseDouble(minPrice);


        Double maximumPriceValue = null;
        if(maxPrice != null && !maxPrice.isEmpty())
            maximumPriceValue = Double.parseDouble(maxPrice);


        //sales = saleService.findAll(pageable);
        sales = saleService.findAll(pageable, craftable, classes, qualities, types, start, end, minimumPriceValue, maximumPriceValue);

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

            // Validate the order
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<Sale>> violations = validator.validate(sale);
            if (!violations.isEmpty()) {
                // Handle validation error
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ \"error\": \"" + "Wrong file format data" + "\" }");
            }

            sales.add(sale);
        }

        //sales.stream().forEach(x-> System.out.println(x));

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


}
