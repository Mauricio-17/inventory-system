package com.mauricio.inventory.views;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "view_equipment")
@Subselect("select uuid() as id, eq.* from view_equipment eq")
@Getter
@Setter
public class CompleteEquipment implements Serializable {

    @Id
    private String id;
    private Long equipmentId;
    private String sku;
    private String name;
    private String status;
    private int stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String brandName;
    private String categoryName;
    private Byte shelfRow;
    private String ownerName;
    private String ownerLastName;
    private Long brandId;
    private Long categoryId;
    private Long locationId;
    private Long ownerId;

}
