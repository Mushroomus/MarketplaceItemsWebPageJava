package com.marketplace.MarketplaceItems.controller;

import com.marketplace.MarketplaceItems.entity.Sale;
import com.marketplace.MarketplaceItems.entity.Item;
import com.marketplace.MarketplaceItems.service.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
    public String showSales() {
        return "sales/show-sales";
    }

    @PostMapping("/fetchCSV")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, CsvException {

        if (file.isEmpty()) {
            return "redirect:/show-sales";
        }

        String[] expectedHeader = {"name", "sku", "orderid", "date", "status", "price", "net", "fee"};

        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));

        List<String[]> records = csvReader.readAll();

        if (!Arrays.equals(records.get(0), expectedHeader)) {
            return "redirect:/show-sales";
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
                return "redirect:/show-sales";
            }

            sales.add(sale);
        }

        sales.stream().forEach(x-> System.out.println(x));

        return "redirect:/show-sales";
    }


}
