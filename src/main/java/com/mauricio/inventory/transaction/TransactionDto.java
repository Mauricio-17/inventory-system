package com.mauricio.inventory.transaction;

public record TransactionDto(
        Long id,
        String description,
        Long equipmentId,
        Long employeeId,
        Long locationProvenance,
        Long locationDestination
) {
}
