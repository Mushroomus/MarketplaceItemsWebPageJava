package com.marketplace.MarketplaceItems.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="items_image")
@Getter
@Setter
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="defindex")
    public Integer defindex;

    @Column(name="image_url")
    public String image_url;

}
