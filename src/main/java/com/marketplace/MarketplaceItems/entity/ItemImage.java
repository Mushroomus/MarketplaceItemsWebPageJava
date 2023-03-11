package com.marketplace.MarketplaceItems.entity;

import javax.persistence.*;

@Entity
@Table(name="items_image")
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="defindex")
    public Integer defindex;

    @Column(name="image_url")
    public String image_url;

    public int getDefindex() {
        return defindex;
    }

    public void setDefindex(int defindex) {
        this.defindex = defindex;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
