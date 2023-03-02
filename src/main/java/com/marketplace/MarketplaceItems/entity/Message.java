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

    @Column(name = "mp_price")
    private String marketplacePrice;

    @Column(name = "craftable")
    private Boolean craftable;

    @Column(name = "class")
    private String itemClass;

    @Column(name = "quality")
    private String quality;

    @Column(name = "type")
    private String type;

    @Transient
    private String username;

    @ManyToOne
    @JoinColumn(name = "sku_item", referencedColumnName = "sku")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

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

    public Double getMarketplacePrice() {

        if(marketplacePrice != null)
            return Double.parseDouble(marketplacePrice);
        else
            return null;
    }

    public void setMarketplacePrice(String marketplacePrice) {
        this.marketplacePrice = marketplacePrice;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @AssertTrue(message = "Delete - lack of informations")
    private boolean isValidDelete() {
        return !"delete".equals(messageType) || item != null;
    }

    @AssertTrue(message = "Update - lack of informations")
    private boolean isValidUpdate() {
        return !"update".equals(messageType) || (item != null && name != null && craftable != null && itemClass != null && quality != null && type != null);
    }

    @AssertTrue(message = "Add - lack of informations")
    private boolean isValidAdd() {
        return !"add".equals(messageType) || (sku != null && name != null && marketplacePrice != null && craftable != null && itemClass != null && quality != null && type != null);
    }

    @AssertTrue(message = "Update price - lack of informations")
    private boolean isValidUpdatePrice() {
        return !"updatePrice".equals(messageType) || (item != null && marketplacePrice != null);
    }

}
