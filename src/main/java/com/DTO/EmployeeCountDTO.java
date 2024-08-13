package com.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCountDTO {
    private String department;
    private String employmentType;
    private Long employeeCount;

   

    // Getters and setters
}
