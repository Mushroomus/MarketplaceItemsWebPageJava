package com.marketplace.MarketplaceItems.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "sku")
    private String sku;

    @Column(name = "name")
    private String name;

    @Getter(AccessLevel.NONE)
    @Setter
    @Column(name = "mp_price")
    private String marketplacePrice;

    @Column(name = "craftable")
    private Boolean craftable;

    @Column(name = "class")
    private String itemClass;

    @Column(name = "quality")
    private String quality;

    @Column(name = "type")
    private String type;

    @Column(name="date")
    private LocalDateTime date;

    @Transient
    private String username;

    @ManyToOne
    @JoinColumn(name = "sku_item", referencedColumnName = "sku")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;


    public Double getMarketplacePrice() {
        if(marketplacePrice != null)
            return Double.parseDouble(marketplacePrice);
        else
            return null;
    }

    @AssertTrue(message = "Delete - lack of informations")
    private boolean isValidDelete() {
        return !"delete".equals(messageType) || item != null;
    }

    @AssertTrue(message = "Update - lack of informations")
    private boolean isValidUpdate() {
        return !"update".equals(messageType) || (item != null && name != null && craftable != null && itemClass != null && quality != null && type != null);
    }

    @AssertTrue(message = "Add - lack of informations")
    private boolean isValidAdd() {
        return !"add".equals(messageType) || (sku != null && name != null && marketplacePrice != null && craftable != null && itemClass != null && quality != null && type != null);
    }

    @AssertTrue(message = "Update price - lack of informations")
    private boolean isValidUpdatePrice() {
        return !"updatePrice".equals(messageType) || (item != null && marketplacePrice != null);
    }

}
