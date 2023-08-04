package com.mauricio.inventory.equipment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class EquipmentDto {

    public Long id;
    public String sku;
    public String name;
    public int stock;
    public String status;
    public Long ownerId;
    public Long categoryId;
    public Long brandId;
    public Long locationId;

}
