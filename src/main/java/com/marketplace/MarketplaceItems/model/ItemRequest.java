package com.marketplace.MarketplaceItems.model;

public class ItemRequest {
    private String sku;
    private int buyHalfScrap;
    private int buyKeys;
    private Integer buyKeyHalfScrap;
    private int sellHalfScrap;
    private int sellKeys;
    private Integer sellKeyHalfScrap;
    private String createdAt;
    private String updatedAt;

    public ItemRequest() {
    }

    public ItemRequest(String sku, int buyHalfScrap, int buyKeys, Integer buyKeyHalfScrap, int sellHalfScrap, int sellKeys, Integer sellKeyHalfScrap, String createdAt, String updatedAt) {
        this.sku = sku;
        this.buyHalfScrap = buyHalfScrap;
        this.buyKeys = buyKeys;
        this.buyKeyHalfScrap = buyKeyHalfScrap;
        this.sellHalfScrap = sellHalfScrap;
        this.sellKeys = sellKeys;
        this.sellKeyHalfScrap = sellKeyHalfScrap;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getBuyHalfScrap() {
        return buyHalfScrap;
    }

    public void setBuyHalfScrap(int buyHalfScrap) {
        this.buyHalfScrap = buyHalfScrap;
    }

    public int getBuyKeys() {
        return buyKeys;
    }

    public void setBuyKeys(int buyKeys) {
        this.buyKeys = buyKeys;
    }

    public Integer getBuyKeyHalfScrap() {
        return buyKeyHalfScrap;
    }

    public void setBuyKeyHalfScrap(Integer buyKeyHalfScrap) {
        this.buyKeyHalfScrap = buyKeyHalfScrap;
    }

    public int getSellHalfScrap() {
        return sellHalfScrap;
    }

    public void setSellHalfScrap(int sellHalfScrap) {
        this.sellHalfScrap = sellHalfScrap;
    }

    public int getSellKeys() {
        return sellKeys;
    }

    public void setSellKeys(int sellKeys) {
        this.sellKeys = sellKeys;
    }

    public Integer getSellKeyHalfScrap() {
        return sellKeyHalfScrap;
    }

    public void setSellKeyHalfScrap(Integer sellKeyHalfScrap) {
        this.sellKeyHalfScrap = sellKeyHalfScrap;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ItemRequest{" +
                "sku='" + sku + '\'' +
                ", buyHalfScrap=" + buyHalfScrap +
                ", buyKeys=" + buyKeys +
                ", buyKeyHalfScrap=" + buyKeyHalfScrap +
                ", sellHalfScrap=" + sellHalfScrap +
                ", sellKeys=" + sellKeys +
                ", sellKeyHalfScrap=" + sellKeyHalfScrap +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}