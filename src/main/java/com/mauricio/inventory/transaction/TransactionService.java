package com.mauricio.inventory.transaction;

import com.mauricio.inventory.employee.Employee;
import com.mauricio.inventory.equipment.Equipment;
import com.mauricio.inventory.exceptions.BadRequestException;
import com.mauricio.inventory.exceptions.ResourceNotFoundException;
import com.mauricio.inventory.exceptions.UnauthorizedRequestException;
import com.mauricio.inventory.location.Location;
import com.mauricio.inventory.location.LocationRepository;
import com.mauricio.inventory.views.CompleteTransaction;
import com.mauricio.inventory.views.CompleteTransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private TransactionRepository transactionRepository;
    private CompleteTransactionRepository completeTransactionRepository;

    public List<TransactionDto> getAllItems( ){
        return transactionRepository.findAll().stream().map(entityToDto).collect(Collectors.toList());
    }

    public List<CompleteTransaction> getCompleteTransactions(){
        return completeTransactionRepository.findAll();
    }

    public void addItem(TransactionDto req){
        Transaction transaction = new Transaction();
        transaction.setDescription(req.description());
        transaction.setEquipment(new Equipment(req.equipmentId()));
        transaction.setEmployee(new Employee(req.employeeId()));
        transaction.setLocationProvenance(req.locationProvenance());
        transaction.setLocationDestination(req.locationDestination());
        transactionRepository.save(transaction);
    }

    public void removeItem(Long id ){
        Optional<Transaction> foundTransaction = transactionRepository.findById(id);
        if (foundTransaction.isEmpty()){
            throw new ResourceNotFoundException(String.format("Transacción con el ID %s no encontrado", id));
        }
        transactionRepository.delete(foundTransaction.get());
    }

    private final Function<Transaction, TransactionDto> entityToDto = item ->
            new TransactionDto(
                    item.getId(),
                    item.getDescription(),
                    item.getEquipment().getId(),
                    item.getEmployee().getId(),
                    item.getLocationProvenance(),
                    item.getLocationDestination()
            );
}
