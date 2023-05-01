package com.marketplace.MarketplaceItems.model;

import java.util.ArrayList;
import java.util.List;

public class SalesItemsDTO {
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