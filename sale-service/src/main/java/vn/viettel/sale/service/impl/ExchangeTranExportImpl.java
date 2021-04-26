package vn.viettel.sale.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class ExchangeTranExportImpl {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private CellStyle headerStyle;

    public ExchangeTranExportImpl() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Hàng_hỏng");
        ////////// HEADER /////////////////////////////
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H1:M1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
        Row customerRow = sheet.createRow(0); // name
        Row customerAddressRow = sheet.createRow(1); // address
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setItalic(true);
        headerFont.setFontHeight(15);
        headerFont.setFontName("Times New Roman");
        headerStyle.setFont(headerFont);
        CellStyle addressStyle = workbook.createCellStyle();
        XSSFFont addressFont = workbook.createFont();
        addressFont.setItalic(true);
        addressFont.setFontHeight(11);
        addressFont.setFontName("Times New Roman");
        addressStyle.setFont(addressFont);
        Row customerPhoneRow = sheet.createRow(2);// phone

        createCell(customerRow, 0,"CH GTSP Hải Dương",headerStyle);
        createCell(customerRow, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM",headerStyle);
        createCell(customerAddressRow, 0,"8 Hoàng Hoa Thám - Hải Dương",addressStyle);
        createCell(customerAddressRow, 9, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",addressStyle);
        createCell(customerPhoneRow, 0, "Tel: (84.320) 3 838 399  Fax: ",addressStyle);
        createCell(customerPhoneRow, 9, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",addressStyle);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:L6"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A8:L8"));
        Row header = sheet.createRow(5);
        Row dateRow = sheet.createRow(7);
        Row row = sheet.createRow(8);
        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont fontTitle = workbook.createFont();
        fontTitle.setFontHeight(15);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);
        titleStyle.setFont(fontTitle);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        XSSFFont fontDate = workbook.createFont();
        fontDate.setFontHeight(12);
        fontDate.setFontName("Times New Roman");
        fontDate.setItalic(true);
        dateStyle.setFont(fontDate);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle colNameStyle = workbook.createCellStyle();
        colNameStyle = workbook.createCellStyle();
        XSSFFont colNameFont = workbook.createFont();
        colNameFont.setFontHeight(10);
        colNameFont.setFontName("Times New Roman");
        colNameFont.setBold(true);
        colNameStyle.setFont(colNameFont);
        colNameStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        colNameStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        colNameStyle.setBorderBottom(BorderStyle.THIN);
        colNameStyle.setBorderTop(BorderStyle.THIN);
        colNameStyle.setBorderLeft(BorderStyle.THIN);
        colNameStyle.setBorderRight(BorderStyle.THIN);
        colNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        createCell(header, 0, "BẢNG TỔNG HỢP ĐỔI HÀNG HƯ HỎNG", titleStyle);
        createCell(dateRow, 0, "TỪ NGÀY: 01/04/2021   ĐẾN NGÀY: 12/04/2021", dateStyle);
        createCell(row, 0, "STT", colNameStyle);
        createCell(row, 1, "NGÀY BIÊN BẢN", colNameStyle);
        createCell(row, 2, "SỐ BIÊN BẢN", colNameStyle);
        createCell(row, 3, "MÃ KHÁCH HÀNG", colNameStyle);
        createCell(row, 4, "TÊN KHÁCH HÀNG", colNameStyle);
        createCell(row, 5, "ĐỊA CHỈ", colNameStyle);
        createCell(row, 6, "MÃ SẢN PHẨM", colNameStyle);
        createCell(row, 7, "TÊN SẢN PHẨM", colNameStyle);
        createCell(row, 8, "SỐ LƯỢNG", colNameStyle);
        createCell(row, 9, "THÀNH TIỀN", colNameStyle);
        createCell(row, 10, "LÍ DO", colNameStyle);
        createCell(row, 11, "SỐ ĐT", colNameStyle);
    }
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if(value instanceof Float) {
            cell.setCellValue((Float)value);
        }else if(value instanceof Double) {
            cell.setCellValue((Double) value);
        }else if(value instanceof Long) {
            cell.setCellValue((Long) value);
        }
        else{
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
}