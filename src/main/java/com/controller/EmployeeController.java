package com.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DTO.EmployeeBarChart;
import com.DTO.EmployeeCountDTO;
import com.DTO.EmployeeCrossDTO;
import com.entities.Employee;
import com.services.EmployeeService;

import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping
	public ResponseEntity<List<Employee>> getAllEmployees() {
		List<Employee> employees = employeeService.getAllEmployees();
		return ResponseEntity.ok(employees);
	}

	 @GetMapping("/")
	    public ResponseEntity<InputStreamResource> downloadReport() throws FileNotFoundException {
	        try {
	            ByteArrayOutputStream byteArrayOutputStream = employeeService.generateReport();
	            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

	            HttpHeaders headers = new HttpHeaders();
	            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Employee_report.pdf");

	            return ResponseEntity
	                    .ok()
	                    .headers(headers)
	                    .contentType(MediaType.APPLICATION_PDF)
	                    .body(new InputStreamResource(byteArrayInputStream));

	        } catch (JRException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }

	 }
	 
	  @GetMapping("/c")
	    public ResponseEntity<List<EmployeeCrossDTO>> getEmployeeCrossTab() {
	        try {
	            List<EmployeeCrossDTO> crossTabData = employeeService.getEmployeeCrossTab();
	            return ResponseEntity.ok(crossTabData);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

	  
	    @GetMapping("/b")
	    public ResponseEntity<List<EmployeeBarChart>> getEmployeeBarChart() {
	        try {
	            List<EmployeeBarChart> barChartData = employeeService.getEmployeeBarChart();
	            return ResponseEntity.ok(barChartData);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

	    @GetMapping("/co")
	    public ResponseEntity<List<EmployeeCountDTO>>getEmployeeCountByDepartmentAndEmploymentType() {
	        try {
	            List<EmployeeCountDTO> barChartData = employeeService.getEmployeeCountByDepartmentAndEmploymentType();
	            return ResponseEntity.ok(barChartData);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

}
