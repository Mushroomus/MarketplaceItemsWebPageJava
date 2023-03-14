package com.marketplace.MarketplaceItems.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="lists")
@Data
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

    public int getItemCount() {
        return itemList.size();
    }

}
