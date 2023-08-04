package com.mauricio.inventory.employee;

import com.mauricio.inventory.exceptions.BadRequestException;
import com.mauricio.inventory.exceptions.ResourceNotFoundException;
import com.mauricio.inventory.role.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public record EmployeeResponse(String name, String lastName, String email,
                               String status){}

    public List<EmployeeDto> getAllItems( ){
        List<EmployeeDto> response = new ArrayList<>();
        employeeRepository.findAll().forEach(item -> {
            response.add(new EmployeeDto(
                    item.getId(), item.getName(), item.getLastName(), item.getEmail(), item.getStatus().toString(), item.getCreatedAt().format(dateTimeFormatter), item.getUpdatedAt().format(dateTimeFormatter)
            ));
        });
        return response;
    }

    public EmployeeDto getItem(Long id ){
        Employee foundEmployee = employeeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Empleado con el ID %s no encontrada", id))
        );
        return new EmployeeDto(foundEmployee.getId(),
                foundEmployee.getName(),
                foundEmployee.getLastName(),
                foundEmployee.getEmail(),
                foundEmployee.getStatus().toString(),
                foundEmployee.getCreatedAt().format(dateTimeFormatter),
                foundEmployee.getUpdatedAt().format(dateTimeFormatter));
    }

    public void addItem(EmployeeDto employeeResponse ){
        if(employeeRepository.existsEmail(employeeResponse.getEmail())){
            throw new BadRequestException("La contraseña o el email ya se encuentran registrados");
        }
        Employee employee = Employee.builder().name(employeeResponse.getName())
                        .lastName(employeeResponse.getLastName())
                        .email(employeeResponse.getEmail())
                        .status(Status.valueOf(employeeResponse.getStatus()))
                        .updatedAt(LocalDateTime.now()).build();
        employeeRepository.save(employee);
    }

    public void updateItem(EmployeeDto employeeResponse, Long id ){
        employeeRepository.findById(id).map(emp -> {
            emp.setName(employeeResponse.getName());
            emp.setLastName(employeeResponse.getLastName());
            emp.setStatus(Status.valueOf(employeeResponse.getStatus()));
            emp.setEmail(employeeResponse.getEmail());
            emp.setUpdatedAt(LocalDateTime.now());
            return employeeRepository.save(emp);
        }).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Empleado con el ID %s no encontrada", id))
        );
    }

    public void removeItem(Long id ){
        Optional<Employee> foundEmployee = employeeRepository.findById(id);
        if (foundEmployee.isEmpty()){
            throw new ResourceNotFoundException(String.format("Empleado con el ID %s no encontrada", id));
        }
        employeeRepository.delete(foundEmployee.get());
    }
}
