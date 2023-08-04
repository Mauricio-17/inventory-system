package com.mauricio.inventory.views;

import java.util.List;

public record CompleteEquipmentPageDto(
        List<CompleteEquipment> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}
