package com.service;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.connection.JDBCConnectionUtil;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ReportService {
	

    public byte[] generateEmployeeReport() throws Exception {
    	
    	String sqlQuery="SELECT name,\r\n"
    			+ "	salary,\r\n"
    			+ "	email,\r\n"
    			+ "	department\r\n"
    			+ "FROM employee";
        // Load the Jasper report from the resources folder
        InputStream reportStream = getClass().getResourceAsStream("/reports/employee.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Get a connection from the utility class
        Connection connection = null;
        byte[] reportBytes = null;

        try {
            connection = JDBCConnectionUtil.getConnection();

            // Execute the SQL query
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();

            // Wrap the result set into JRResultSetDataSource
            JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultSet);

            // Parameters for the report, if needed
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Tableparm", resultSetDataSource);
            

            // Fill the report with data
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters ,resultSetDataSource);

            // Export the report to PDF format
            reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        } finally {
            // Ensure the connection is closed
            JDBCConnectionUtil.closeConnection(connection);
        }

        return reportBytes;
    }
}
