package com.marketplace.MarketplaceItems.model;

public class CreateListRequest {
    private String username;
    private java.util.List<String> itemSku;
    private String listName;

    public CreateListRequest() { }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public java.util.List<String> getItemSku() {
        return itemSku;
    }

    public void setItemSku(java.util.List<String> itemSku) {
        this.itemSku = itemSku;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}