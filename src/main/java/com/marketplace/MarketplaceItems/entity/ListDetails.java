package com.marketplace.MarketplaceItems.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="lists")
@Data
public class ListDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="date")
    private LocalDateTime date;

    @OneToMany(mappedBy = "list")
    private List<ItemList> itemList;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public int getItemCount() {
        return itemList.size();
    }

}
