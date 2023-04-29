package com.marketplace.MarketplaceItems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.sql.DataSourceDefinition;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @Column(name="sku")
    private String sku;

    @Column(name="name")
    private String name;

    @Column(name="mp_price")
    private Double marketplacePrice;

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

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE)
    private List<ItemList> itemLists;

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE)
    private List<Message> messages;
}
