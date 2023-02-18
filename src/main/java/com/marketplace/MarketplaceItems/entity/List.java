package com.marketplace.MarketplaceItems.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="lists")
public class List {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="date")
    private LocalDateTime date;


    @OneToMany(mappedBy = "list")
    private java.util.List<ItemList> itemList;

    public java.util.List<ItemList> getUserItemList() {
        return itemList;
    }

    public void setUserItemList(java.util.List<ItemList> itemList) {
        this.itemList = itemList;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
