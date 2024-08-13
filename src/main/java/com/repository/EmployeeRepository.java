package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import com.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
	

	   @Procedure(name = "GetEmployeeBasicInfo")
	    List<Object[]> getEmployeeBasicInfo();

	    @Procedure(name = "GetTotalSalaryByDepartment")
	    List<Object[]> getTotalSalaryByDepartment();
	    
	    @Procedure(name = "GetEmployeeCountByDepartmentAndEmploymentType")
	    List<Object[]> getEmployeeCountByDepartmentAndEmploymentType();

}
