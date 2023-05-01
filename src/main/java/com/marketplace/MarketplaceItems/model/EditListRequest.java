package com.marketplace.MarketplaceItems.model;

public class EditListRequest {
    private java.util.List<String> itemSku;
    private String listName;
    public EditListRequest() { }
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