package com.marketplace.MarketplaceItems.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Data
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

    @JsonIgnore
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
}
