package vn.viettel.customer.service.impl;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.service.dto.ExportCustomerDTO;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class CustomerExcelExporter {

    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private List<Customer> customers;
    Map<Long, String> customerTypeMaps;
    Map<Long, String> closelyTypeMaps;
    Map<Long, String> cardTypeMaps;

    public CustomerExcelExporter(List<Customer> customers,   Map<Long, String> customerTypeMaps, Map<Long, String> closelyTypeMaps, Map<Long, String> cardTypeMaps) {
        this.customers = customers;
        this.customerTypeMaps = customerTypeMaps;
        this.closelyTypeMaps = closelyTypeMaps;
        this.cardTypeMaps = cardTypeMaps;
        workbook = new SXSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Customers");

        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        Row row5 = sheet.createRow(5);
                ////////////////////////////////////////////////////////////////////
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontHeight(20);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        row.setRowStyle(style);
        //////////////////////////////////////////////////////////////////
        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = (XSSFFont) workbook.createFont();
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
        XSSFFont font2 = (XSSFFont) workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName("Times New Roman");
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row5.setRowStyle(style2);
        //////////////////////////////////////////////////////////////////
        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontheader = (XSSFFont) workbook.createFont();
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
        XSSFFont fontHeader1 = (XSSFFont) workbook.createFont();
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

        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:V1"));
        ExcelPoiUtils.createCell(row, 0, "DANH SÁCH KHÁCH HÀNG", styleHeader1);
        ExcelPoiUtils.createCell(row1, 0, "STT", styleHeader);
        ExcelPoiUtils.createCell(row1, 1, "Mã KH", styleHeader);
        ExcelPoiUtils.createCell(row1, 2, "Họ tên KH", styleHeader);
        ExcelPoiUtils.createCell(row1, 3, "Mã vạch", styleHeader);
        ExcelPoiUtils.createCell(row1, 4, "Ngày sinh", styleHeader);
        ExcelPoiUtils.createCell(row1, 5, "Giới tính", styleHeader);
        ExcelPoiUtils.createCell(row1, 6, "Nhóm KH", styleHeader);
        ExcelPoiUtils.createCell(row1, 7, "Trạng thái", styleHeader);
        ExcelPoiUtils.createCell(row1, 8, "KH riêng Cửa hàng", styleHeader);
        ExcelPoiUtils.createCell(row1, 9, "CMND", styleHeader);
        ExcelPoiUtils.createCell(row1, 10, "Ngày cấp", styleHeader);
        ExcelPoiUtils.createCell(row1, 11, "Nơi cấp", styleHeader);
        ExcelPoiUtils.createCell(row1, 12, "Di động", styleHeader);
        ExcelPoiUtils.createCell(row1, 13, "Email", styleHeader);
        ExcelPoiUtils.createCell(row1, 14, "Địa chỉ", styleHeader);
        ExcelPoiUtils.createCell(row1, 15, "Cơ quan", styleHeader);
        ExcelPoiUtils.createCell(row1, 16, "Địa chỉ cơ quan", styleHeader);
        ExcelPoiUtils.createCell(row1, 17, "Mã số thuế", styleHeader);
        ExcelPoiUtils.createCell(row1, 18, "Loại thẻ", styleHeader);
        ExcelPoiUtils.createCell(row1, 19, "Loại KH", styleHeader);
        ExcelPoiUtils.createCell(row1, 20, "Ngày tạo", styleHeader);
        ExcelPoiUtils.createCell(row1, 21, "Ghi chú", styleHeader);
        row.setHeight((short) 800);
        row1.setHeight((short) 650);
        ExcelPoiUtils.autoSizeAllColumns(sheet, 21);
    }

    private void writeDataLines() {
        int rowCount = 2;
        int stt = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
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
        XSSFFont font1 = (XSSFFont) workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style1.setFont(font1);
        style1.setAlignment(HorizontalAlignment.CENTER);
        style1.setVerticalAlignment(VerticalAlignment.CENTER);
        style1.setBorderTop(BorderStyle.THIN);
        style1.setBorderBottom(BorderStyle.THIN);
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setBorderRight(BorderStyle.THIN);
        for (Customer customer: customers) {
            stt++;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            ExcelPoiUtils.createCell(row, columnCount++, stt, style1);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getCustomerCode()), style);
            ExcelPoiUtils.createCell(row, columnCount++, customer.getLastName() + " " + customer.getFirstName(), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getBarCode()), style);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dob="";
            if(customer.getDob()!=null){
                dob = formatter.format(customer.getDob());
            }
            ExcelPoiUtils.createCell(row, columnCount++, dob, style);

            if (customer.getGenderId() == null) {
                ExcelPoiUtils.createCell(row, columnCount++, "", style);
            } else if (customer.getGenderId() == 1) {
                ExcelPoiUtils.createCell(row, columnCount++, "Nam", style);
            } else if (customer.getGenderId() == 2) {
                ExcelPoiUtils.createCell(row, columnCount++, "Nữ", style);
            } else {
                ExcelPoiUtils.createCell(row, columnCount++, "Khác", style);
            }
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(this.getCustomerTypeName(customer.getCustomerTypeId())), style);
            if (customer.getStatus() == 1) {
                ExcelPoiUtils.createCell(row, columnCount++, "Hoạt động", style);
            } else {
                ExcelPoiUtils.createCell(row, columnCount++, "Ngưng hoạt động", style);
            }

            if (customer.getIsPrivate() == null) {
                ExcelPoiUtils.createCell(row, columnCount++, " ", style);
            } else if (customer.getIsPrivate() == true) {
                ExcelPoiUtils.createCell(row, columnCount++, "Có", style);
            } else {
                ExcelPoiUtils.createCell(row, columnCount++, "Không", style);
            }

            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getIdNo()), style);

            if (customer.getIdNoIssuedDate() == null){
                ExcelPoiUtils.createCell(row, columnCount++, "", style);
            }else {
                String idNoIssuedDate = formatter.format(customer.getIdNoIssuedDate());
                ExcelPoiUtils.createCell(row, columnCount++, idNoIssuedDate, style);
            }
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getIdNoIssuedPlace()), style);
            ExcelPoiUtils.createCell(row, columnCount++, customer.getMobiPhone(), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getEmail()), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getAddress()), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getWorkingOffice()), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getOfficeAddress()), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getTaxCode()), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(this.getMemberCardName(customer.getCardTypeId())), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(this.getCloselyName(customer.getCloselyTypeId())), style);
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String createdAt = null;
            if(customer.getCreatedAt()!=null) createdAt = formatter1.format(customer.getCreatedAt());

            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(createdAt), style);
            ExcelPoiUtils.createCell(row, columnCount++, this.checkNull(customer.getNoted()), style);
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet, 21);
    }

    private String checkNull(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    private String getCustomerTypeName(Long id){
        String type = "";
        if(customerTypeMaps.containsKey(id))
            type = customerTypeMaps.get(id);
        return type;
    }

    private String getMemberCardName(Long id){
        String type = "";
        if(cardTypeMaps.containsKey(id))
            type = cardTypeMaps.get(id);
        return type;
    }

    private String getCloselyName(Long id){
        String type = "";
        if(closelyTypeMaps.containsKey(id))
            type = closelyTypeMaps.get(id);
        return type;
    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        IOUtils.closeQuietly(out);
        System.gc();
        return response;
    }
}