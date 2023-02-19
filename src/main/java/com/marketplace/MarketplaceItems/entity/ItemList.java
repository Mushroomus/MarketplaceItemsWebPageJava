package com.marketplace.MarketplaceItems.entity;

import javax.persistence.*;

@Entity
@Table(name="list_items")
public class ItemList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_item")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "id_list")
    private List list;


    public ItemList() {

    }

    public ItemList(Item item, User user, List list) {
        this.item = item;
        this.user = user;
        this.list = list;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List getList() {
        return list;
    }

    public void setList(List itemList) {
        this.list = itemList;
    }
}
