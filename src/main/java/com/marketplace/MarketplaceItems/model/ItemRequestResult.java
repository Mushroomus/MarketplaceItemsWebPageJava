package com.marketplace.MarketplaceItems.model;

public class ItemRequestResult {
    private String sku;
    private String name;
    private String marketplacePrice;

    private String backpackPrice;

    private String calculatedProfit;

    public ItemRequestResult(String sku, String name, String marketplacePrice, String backpackPrice, String calculatedProfit) {
        this.sku = sku;
        this.name = name;
        this.marketplacePrice = marketplacePrice;
        this.backpackPrice = backpackPrice;
        this.calculatedProfit = calculatedProfit;
    }

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

    public String getMarketplacePrice() {
        return marketplacePrice;
    }

    public void setMarketplacePrice(String marketplacePrice) {
        this.marketplacePrice = marketplacePrice;
    }

    public String getBackpackPrice() {
        return backpackPrice;
    }

    public void setBackpackPrice(String backpackPrice) {
        this.backpackPrice = backpackPrice;
    }

    public String getCalculatedProfit() {
        return calculatedProfit;
    }

    public void setCalculatedProfit(String calculatedProfit) {
        this.calculatedProfit = calculatedProfit;
    }
}