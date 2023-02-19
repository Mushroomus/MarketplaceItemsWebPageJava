package com.marketplace.MarketplaceItems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="role")
    private String role;

    @Column(name="date")
    private LocalDateTime date;

    @OneToMany(mappedBy = "user")
    private List<ItemList> itemList;



    public class ListInfoModel {
        private String name;
        private LocalDateTime date;
        private int itemsCount;

        public ListInfoModel(String name, LocalDateTime date, int itemsCount) {
            this.name = name;
            this.date = date;
            this.itemsCount = itemsCount;
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

        public int getItemsCount() {
            return itemsCount;
        }

        public void setItemsCount(int itemsCount) {
            this.itemsCount = itemsCount;
        }
    }


    public List<ListInfoModel> getListNamesWithItemCount() {

        List<ListInfoModel> result = new ArrayList<>();

        for (ItemList record : itemList) {

            String listName = record.getList().getName();
            LocalDateTime createListDate = record.getList().getDate();
            int listItemNumber = record.getList().getItemCount();

            boolean found = false;

            for (ListInfoModel model : result) {
                if (model.getName().equals(listName)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                result.add(new ListInfoModel(listName, createListDate, listItemNumber));
            }
        }

        return result;
    }

    @JsonIgnore
    public List<ItemList> getUserItemList() {
        return itemList;
    }

    @JsonIgnore
    public void setUserItemList(List<ItemList> itemList) {
        this.itemList = itemList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
