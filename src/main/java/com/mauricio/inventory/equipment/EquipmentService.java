package com.mauricio.inventory.equipment;

import com.mauricio.inventory.brand.Brand;
import com.mauricio.inventory.brand.BrandRepository;
import com.mauricio.inventory.category.Category;
import com.mauricio.inventory.category.CategoryRepository;
import com.mauricio.inventory.exceptions.BadRequestException;
import com.mauricio.inventory.exceptions.ResourceNotFoundException;
import com.mauricio.inventory.exceptions.UnauthorizedRequestException;
import com.mauricio.inventory.location.Location;
import com.mauricio.inventory.location.LocationRepository;
import com.mauricio.inventory.location.Status;
import com.mauricio.inventory.owner.Owner;
import com.mauricio.inventory.owner.OwnerRepository;
import com.mauricio.inventory.views.CompleteEquipment;
import com.mauricio.inventory.views.CompleteEquipmentPageDto;
import com.mauricio.inventory.views.CompleteEquipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EquipmentService {

    private EquipmentRepository equipmentRepository;
    private CategoryRepository categoryRepository;
    private OwnerRepository ownerRepository;
    private BrandRepository brandRepository;
    private LocationRepository locationRepository;
    private CompleteEquipmentRepository completeEquipmentRepository;

    public void dataValidation(EquipmentDto equipment){

        Optional<Category> category = categoryRepository.findById(equipment.getCategoryId());
        Optional<Owner> owner = ownerRepository.findById(equipment.getOwnerId());
        Optional<Brand> brand = brandRepository.findById(equipment.getBrandId());
        Optional<Location> location = locationRepository.findById(equipment.getLocationId());

        if(category.isEmpty() || owner.isEmpty() || brand.isEmpty() || location.isEmpty()){
            throw new BadRequestException("Error en algún dato de categoría, propietario, " +
                    "marca o ubicación.");
        }
        boolean isAvailable = location.get().getStatus() == Status.AVAILABLE;
        if( !isAvailable){
            throw new BadRequestException("El estante no está disponible");
        }
    }

    public List<EquipmentDto> getAllItems( ){
        return equipmentRepository.findAll().stream().map(entityToDto).collect(Collectors.toList());
    }

    public EquipmentPageDto getEquipmentsByQueries(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Equipment> equipments = equipmentRepository.findAll(pageable);
        List<Equipment> listOfEquipments = equipments.getContent();
        List<EquipmentDto> content = listOfEquipments.stream().map(entityToDto).toList();

        return new EquipmentPageDto(
                content,
                equipments.getNumber(),
                equipments.getSize(),
                equipments.getTotalElements(),
                equipments.getTotalPages(),
                equipments.isLast()
        );
    }

    public CompleteEquipmentPageDto getCompleteEquipmentsByQueries(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<CompleteEquipment> completeEquipments = completeEquipmentRepository.findAll(pageable);
        List<CompleteEquipment> listOfCompleteEquipments = completeEquipments.getContent();

        return new CompleteEquipmentPageDto(
                listOfCompleteEquipments,
                completeEquipments.getNumber(),
                completeEquipments.getSize(),
                completeEquipments.getTotalElements(),
                completeEquipments.getTotalPages(),
                completeEquipments.isLast()
        );
    }
    public EquipmentDto getItem(Long id){
        Equipment foundEquipment = equipmentRepository.findById(id).orElse(null);
        return (foundEquipment != null) ? entityToDto.apply(foundEquipment) : null;
    }

    public void addItem(EquipmentDto req){
        dataValidation(req);
        if(equipmentRepository.existsName(req.getName())){
            throw new BadRequestException("El nombre ya está registrado");
        }
        Equipment equipmentSaved = new Equipment();
        equipmentSaved.setName(req.getName());
        equipmentSaved.setOwner(new Owner(req.getOwnerId()));
        equipmentSaved.setBrand(new Brand(req.getBrandId()));
        equipmentSaved.setCategory(new Category(req.getCategoryId()));
        equipmentSaved.setLocation(new Location(req.getLocationId()));
        equipmentSaved.setStock(req.getStock());
        equipmentSaved.setSku(req.getSku());
        equipmentSaved.setStatus(com.mauricio.inventory.equipment.Status.valueOf(req.getStatus()));
        equipmentRepository.save(equipmentSaved);
    }

    public void updateItem(EquipmentDto req, Long id ){
        dataValidation(req);
        equipmentRepository.findById(id).map(eq -> {
            eq.setName(req.getName());
            eq.setStock(req.getStock());
            eq.setStatus(com.mauricio.inventory.equipment.Status.valueOf(req.getStatus()));
            eq.setStock(req.getStock());
            eq.setOwner(new Owner(req.getOwnerId()));
            eq.setCategory(new Category(req.getCategoryId()));
            eq.setBrand(new Brand(req.getBrandId()));
            eq.setLocation(new Location(req.getLocationId()));
            return equipmentRepository.save(eq);
        }).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Equipo con el ID %s no encontrada", id))
        );
    }

    public void removeItem(Long id ){
        Optional<Equipment> foundEquipment = equipmentRepository.findById(id);
        if(foundEquipment.isEmpty()){
            throw new ResourceNotFoundException(String.format("Equipo con el ID %s no encontrada", id));
        }
        equipmentRepository.delete(foundEquipment.get());
    }

    private final Function<Equipment, EquipmentDto> entityToDto = item ->
            new EquipmentDto(
                    item.getId(),
                    item.getSku(),
                    item.getName(),
                    item.getStock(),
                    item.getStatus().toString(),
                    item.getOwner().getId(),
                    item.getCategory().getId(),
                    item.getBrand().getId(),
                    item.getLocation().getId()
            );
}
