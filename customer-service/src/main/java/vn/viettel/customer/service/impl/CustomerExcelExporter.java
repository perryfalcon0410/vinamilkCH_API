package vn.viettel.customer.service.impl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import vn.viettel.customer.service.dto.CustomerResponse;

public class CustomerExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<CustomerResponse> customerList;
//    @Value( "${file.upload-dir}")
    private String UPLOADED_FOLDER = "D:/INTERN/";

    public CustomerExcelExporter(List<CustomerResponse> customerList) {
        this.customerList = customerList;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Customers");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row.setRowStyle(style);

        createCell(row, 0, "Customer Code", style);
        createCell(row, 1, "Full Name", style);
        createCell(row, 2, "Phone", style);
        createCell(row, 3, "Gender", style);
        createCell(row, 4, "DOB", style);
        createCell(row, 5, "Status", style);
        createCell(row, 6, "Group", style);
        createCell(row, 7, "Address", style);
        createCell(row, 8, "Create date", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (CustomerResponse customer : customerList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, customer.getCusCode(), style);
            createCell(row, columnCount++, customer.getLastName() + " " + customer.getFirstName(), style);
            createCell(row, columnCount++, customer.getPhoneNumber(), style);
            createCell(row, columnCount++, customer.getGender(), style);
            createCell(row, columnCount++, customer.getDOB(), style);
            createCell(row, columnCount++, customer.getStatus(), style);
            createCell(row, columnCount++, customer.getCusGroup(), style);
            createCell(row, columnCount++, customer.getAddress(), style);
            createCell(row, columnCount++, customer.getCreateDate(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        File file = new File(UPLOADED_FOLDER + "/customer_report.xlsx");
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file, false);
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
