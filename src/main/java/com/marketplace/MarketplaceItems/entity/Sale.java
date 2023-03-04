package com.marketplace.MarketplaceItems.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orderId")
    private String orderId;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name="price")
    private Double price;

    @Column(name="net")
    private Double net;

    @Column(name="fee")
    private Double fee;

    @ManyToOne
    @JoinColumn(name = "item_sku", referencedColumnName = "sku")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
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

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", date=" + date +
                ", price=" + price +
                ", net=" + net +
                ", fee=" + fee +
                '}';
    }
}
