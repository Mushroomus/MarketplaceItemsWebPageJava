package com.marketplace.MarketplaceItems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="items")
public class Item {

    @Id
    @Column(name="sku")
    private String sku;

    @Column(name="name")
    private String name;

    @Column(name="craftable")
    private boolean craftable;

    @Column(name="class")
    private String classItem;

    @Column(name="quality")
    private String quality;

    @Column(name="type")
    private String type;

    @Column(name="image")
    private String image;

    @OneToMany(mappedBy = "item")
    private List<ItemList> itemList = new ArrayList<>();


    @JsonIgnore
    public List<ItemList> getUserItemList() {
        return itemList;
    }

    @JsonIgnore
    public void setUserItemList(List<ItemList> itemList) {
        this.itemList = itemList;
    }

    public Item(String sku, String name, boolean craftable, String classItem, String quality, String type, String image) {
        this.sku = sku;
        this.name = name;
        this.craftable = craftable;
        this.classItem = classItem;
        this.quality = quality;
        this.type = type;
        this.image = image;
    }

    public Item() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public void setCraftable(boolean craftable) {
        this.craftable = craftable;
    }

    public String getClassItem() {
        return classItem;
    }

    public void setClassItem(String classItem) {
        this.classItem = classItem;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Item{" +
                "sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", craftable=" + craftable +
                ", classItem='" + classItem + '\'' +
                ", quality='" + quality + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
