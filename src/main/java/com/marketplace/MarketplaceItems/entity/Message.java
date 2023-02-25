package com.marketplace.MarketplaceItems.entity;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "sku")
    private String sku;

    @Column(name = "name")
    private String name;

    @Column(name = "craftable")
    private Boolean craftable;

    @Column(name = "class")
    private String itemClass;

    @Column(name = "quality")
    private String quality;

    @Column(name = "type")
    private String type;

    @Column(name = "price")
    private Float price;

    @ManyToOne
    @JoinColumn(name = "sku_item", referencedColumnName = "sku")
    private Item item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public Boolean getCraftable() {
        return craftable;
    }

    public void setCraftable(Boolean craftable) {
        this.craftable = craftable;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }


    @AssertTrue(message = "sku_item is required for messageType 'delete'")
    private boolean isValidDelete() {
        return !"delete".equals(messageType) || item != null;
    }

    @AssertTrue(message = "sku_item, craftable, class, quality, and type are required for messageType 'update'")
    private boolean isValidUpdate() {
        return !"update".equals(messageType) || (item != null && craftable != null && itemClass != null && quality != null && type != null);
    }

    @AssertTrue(message = "sku, name, craftable, class, quality, and type are required for messageType 'add'")
    private boolean isValidAdd() {
        return !"add".equals(messageType) || (item != null && name != null && craftable != null && itemClass != null && quality != null && type != null);
    }

    @AssertTrue(message = "sku_item and price are required for messageType 'updatePrice'")
    private boolean isValidUpdatePrice() {
        return !"updatePrice".equals(messageType) || (item != null && price != null);
    }

}
