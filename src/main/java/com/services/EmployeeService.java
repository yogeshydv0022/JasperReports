package com.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DTO.EmployeeBarChart;
import com.DTO.EmployeeCountDTO;
import com.DTO.EmployeeCrossDTO;
import com.entities.Employee;
import com.repository.EmployeeRepository;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    public List<EmployeeCrossDTO> getEmployeeCrossTab() {
        List<Object[]> results = employeeRepository.getEmployeeBasicInfo();
        return results.stream()
                .map(result -> new EmployeeCrossDTO(
                        (String) result[0], // name
                        (String) result[1], // department
                        (String) result[2]  // employmentType
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EmployeeBarChart> getEmployeeBarChart() {
        List<Object[]> results = employeeRepository.getTotalSalaryByDepartment();
        return results.stream()
                .map(result -> new EmployeeBarChart(
                        (String) result[0], // department
                        (Double) result[1]   // totalSalary
                ))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public List<EmployeeCountDTO> getEmployeeCountByDepartmentAndEmploymentType() {
        List<Object[]> results = employeeRepository.getEmployeeCountByDepartmentAndEmploymentType();
        return results.stream()
                .map(result -> new EmployeeCountDTO(
                        (String) result[0], // department
                        (String) result[1], // employment_type
                        (Long) result[2]    // employeeCount
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ByteArrayOutputStream generateReport() throws JRException {
        List<Employee> employees = employeeRepository.findAll();

        if (employees == null || employees.isEmpty()) {
            throw new RuntimeException("No employees found");
        }

        List<EmployeeCrossDTO> employeeCrossTab = getEmployeeCrossTab();
        List<EmployeeCountDTO> employeeBarChart = getEmployeeCountByDepartmentAndEmploymentType();

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);
        JRBeanCollectionDataSource dataSourceCrossTab = new JRBeanCollectionDataSource(employeeCrossTab);
        JRBeanCollectionDataSource dataSourceBarChart = new JRBeanCollectionDataSource(employeeBarChart);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tableData", dataSource);
        parameters.put("crossTab", dataSourceCrossTab);
        parameters.put("barChart", dataSourceBarChart);
       // parameters.put("paiChart", dataSourceBarChart);

        InputStream reportStream = getClass().getResourceAsStream("/reports/demo_report.jrxml");
        log.info("Attempting to load JRXML file from: /reports/emp_reports.jrxml");

        if (reportStream == null) {
            log.error("Report file not found in the classpath: /reports/emp_reports.jrxml");
            throw new RuntimeException("Could not find the report file at the specified path: /reports/emp_reports.jrxml");
        }

        JasperReport jasperReport;
        try {
            jasperReport = JasperCompileManager.compileReport(reportStream);
        } catch (JRException e) {
            log.error("Error compiling the JRXML report file", e);
            throw new RuntimeException("Error compiling the JRXML report file", e);
        }

        JasperPrint jasperPrint;
        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        } catch (JRException e) {
            log.error("Error filling the Jasper report", e);
            throw new RuntimeException("Error filling the Jasper report", e);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        } catch (JRException e) {
            log.error("Error exporting the Jasper report to PDF", e);
            throw new RuntimeException("Error exporting the Jasper report to PDF", e);
        }
        return byteArrayOutputStream;
    }
}
