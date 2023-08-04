package com.mauricio.inventory.equipment;

import com.mauricio.inventory.views.CompleteEquipmentPageDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/equipment")
@AllArgsConstructor
public class EquipmentController {

    private EquipmentService equipmentService;

    @GetMapping(value = "/view")
    public CompleteEquipmentPageDto getCompleteEquipmentsByQueries(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
            ){
        return equipmentService.getCompleteEquipmentsByQueries(pageNo, pageSize);
    }
    @GetMapping
    public EquipmentPageDto getEquipmentsByQueries(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ){
        return equipmentService.getEquipmentsByQueries(pageNo, pageSize);
    }

    @GetMapping("/{id}")
    public EquipmentDto getEquipment(@PathVariable(value = "id") Long id){
        return equipmentService.getItem(id);
    }

    @PostMapping
    public ResponseEntity<Void> addEquipment(@Valid @RequestBody EquipmentDto equipment ){
        equipmentService.addItem(equipment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEquipment(@Valid @RequestBody EquipmentDto equipment, @PathVariable(value = "id") Long id ){
        equipmentService.updateItem(equipment, id );
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable(value = "id") Long id ){
        equipmentService.removeItem(id );
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
