package com.mauricio.inventory.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class EmployeeDto {

    public Long id;
    public String name;
    public String lastName;
    public String email;
    public String status;
    public String createdAt;
    public String updatedAt;

}
