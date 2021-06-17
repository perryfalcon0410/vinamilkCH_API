package vn.viettel.customer.service.impl;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.customer.service.dto.ExportCustomerDTO;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ExportCustomerDTO> customerList;

    public CustomerExcelExporter(List<ExportCustomerDTO> customerList) {
        this.customerList = customerList;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Customers");

        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        Row row5 = sheet.createRow(5);
                ////////////////////////////////////////////////////////////////////
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(20);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        row.setRowStyle(style);
        //////////////////////////////////////////////////////////////////
        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = workbook.createFont();
        font1.setBold(false);
        font1.setItalic(true);
        font1.setFontHeight(11);
        font1.setFontName("Times New Roman");
        style1.setFont(font1);
        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row1.setRowStyle(style1);
        row2.setRowStyle(style1);
        //////////////////////////////////////////////////////////////////
        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName("Times New Roman");
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row5.setRowStyle(style2);
        //////////////////////////////////////////////////////////////////
        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontheader = workbook.createFont();
        fontheader.setFontHeight(12);
        fontheader.setFontName("Times New Roman");
        styleHeader.setFont(fontheader);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);

        ////////////////////////////////////////////////////////

        CellStyle styleHeader1 = workbook.createCellStyle();
        XSSFFont fontHeader1 = workbook.createFont();
        fontHeader1.setFontHeight(20);
        fontHeader1.setFontName("Times New Roman");
        styleHeader1.setFont(fontHeader1);
        styleHeader1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader1.setAlignment(HorizontalAlignment.CENTER);
        styleHeader1.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader1.setBorderTop(BorderStyle.THIN);
        styleHeader1.setBorderBottom(BorderStyle.THIN);
        styleHeader1.setBorderLeft(BorderStyle.THIN);
        styleHeader1.setBorderRight(BorderStyle.THIN);


        byte[] rgb = new byte[]{(byte) 142, (byte) 169, (byte) 219};
        XSSFCellStyle totalRowStyleRGB = (XSSFCellStyle) styleHeader;
        XSSFColor customColor = new XSSFColor(rgb, null);
        totalRowStyleRGB.setFillForegroundColor(customColor);

        XSSFCellStyle totalRowStyleRGB1 = (XSSFCellStyle) styleHeader1;
        XSSFColor customColor1 = new XSSFColor(Color.WHITE, null);
        totalRowStyleRGB1.setFillForegroundColor(customColor1);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:U1"));
        createCell(row, 0, "DANH SÁCH KHÁCH HÀNG", styleHeader1);
        createCell(row1, 0, "STT", styleHeader);
        createCell(row1, 1, "Mã KH", styleHeader);
        createCell(row1, 2, "Họ tên KH", styleHeader);
        createCell(row1, 3, "Mã vạch", styleHeader);
        createCell(row1, 4, "Ngày sinh", styleHeader);
        createCell(row1, 5, "Giới tính", styleHeader);
        createCell(row1, 6, "Nhóm KH", styleHeader);
        createCell(row1, 7, "Trạng thái", styleHeader);
        createCell(row1, 8, "KH riêng Cửa hàng", styleHeader);
        createCell(row1, 9, "CMND", styleHeader);
        createCell(row1, 10, "Ngày cấp", styleHeader);
        createCell(row1, 11, "Nơi cấp", styleHeader);
        createCell(row1, 12, "Di động", styleHeader);
        createCell(row1, 13, "Email", styleHeader);
        createCell(row1, 14, "Địa chỉ", styleHeader);
        createCell(row1, 15, "Cơ quan", styleHeader);
        createCell(row1, 16, "Địa chỉ cơ quan", styleHeader);
        createCell(row1, 17, "Mã số thuế", styleHeader);
        createCell(row1, 18, "Loại thẻ", styleHeader);
        createCell(row1, 19, "Loại KH", styleHeader);
        createCell(row1, 20, "Ngày tạo", styleHeader);
        createCell(row1, 21, "Ghi chú", styleHeader);

        row.setHeight((short) 800);
        row1.setHeight((short) 650);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
        sheet.autoSizeColumn(columnCount);
    }

    private void writeDataLines() {
        int rowCount = 2;
        int stt = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style1.setFont(font1);
        style1.setAlignment(HorizontalAlignment.CENTER);
        style1.setVerticalAlignment(VerticalAlignment.CENTER);
        style1.setBorderTop(BorderStyle.THIN);
        style1.setBorderBottom(BorderStyle.THIN);
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setBorderRight(BorderStyle.THIN);
        for (ExportCustomerDTO customer : customerList) {
            stt++;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, stt, style1);
            createCell(row, columnCount++, this.checkNull(customer.getCustomerCode()), style);
            createCell(row, columnCount++, customer.getLastName() + " " + customer.getFirstName(), style);
            createCell(row, columnCount++, this.checkNull(customer.getBarCode()), style);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dob = formatter.format(customer.getDob());
            createCell(row, columnCount++, dob, style);

            if (customer.getGenderId() == null) {
                createCell(row, columnCount++, "", style);
            } else if (customer.getGenderId() == 1) {
                createCell(row, columnCount++, "Nam", style);
            } else if (customer.getGenderId() == 2) {
                createCell(row, columnCount++, "Nữ", style);
            } else {
                createCell(row, columnCount++, "Khác", style);
            }
            createCell(row, columnCount++, this.checkNull(customer.getCustomerTypeName()), style);
            if (customer.getStatus() == 1) {
                createCell(row, columnCount++, "Hoạt động", style);
            } else {
                createCell(row, columnCount++, "Ngưng hoạt động", style);
            }

            if (customer.getIsPrivate() == null) {
                createCell(row, columnCount++, " ", style);
            } else if (customer.getIsPrivate() == true) {
                createCell(row, columnCount++, "Có", style);
            } else {
                createCell(row, columnCount++, "Không", style);
            }

            createCell(row, columnCount++, this.checkNull(customer.getIdNo()), style);

            if (customer.getIdNoIssuedDate() == null){
                createCell(row, columnCount++, "", style);
            }else {
                String idNoIssuedDate = formatter.format(customer.getIdNoIssuedDate());
                createCell(row, columnCount++, idNoIssuedDate, style);
            }
            createCell(row, columnCount++, this.checkNull(customer.getIdNoIssuedPlace()), style);
            createCell(row, columnCount++, customer.getMobiPhone(), style);
            createCell(row, columnCount++, this.checkNull(customer.getEmail()), style);
            createCell(row, columnCount++, this.checkNull(customer.getAddress()), style);
            createCell(row, columnCount++, this.checkNull(customer.getWorkingOffice()), style);
            createCell(row, columnCount++, this.checkNull(customer.getOfficeAddress()), style);
            createCell(row, columnCount++, this.checkNull(customer.getTaxCode()), style);
            createCell(row, columnCount++, this.checkNull(customer.getMemberCardName()), style);
            createCell(row, columnCount++, this.checkNull(customer.getApParamName()), style);
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String createdAt = formatter1.format(customer.getCreatedAt());
            createCell(row, columnCount++, this.checkNull(createdAt), style);
            createCell(row, columnCount++, this.checkNull(customer.getNoted()), style);
        }
    }

    public String checkNull(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }


    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}