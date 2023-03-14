package com.marketplace.MarketplaceItems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="sales")
@Data
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name="price")
    private Double price;

    @Column(name="net")
    private Double net;

    @Column(name="fee")
    private Double fee;

    @Column(name="assign_sku")
    private String assignSku;

    @ManyToOne
    @JoinColumn(name = "item_sku", referencedColumnName = "sku")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
}
