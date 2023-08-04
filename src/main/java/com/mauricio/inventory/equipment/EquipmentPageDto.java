package com.mauricio.inventory.equipment;
import java.util.List;
public record EquipmentPageDto(
        List<EquipmentDto> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}
