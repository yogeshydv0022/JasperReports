package com.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.ReportService;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/report/")
    public ResponseEntity<byte[]> generateEmployeeReport() {
        try {
            // Generate the report using the service
            byte[] report = reportService.generateEmployeeReport();

            // Set up the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=employeeReport.pdf");

            // Return the PDF report in the response
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(report);

        } catch (Exception e) {
            // Handle errors
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
