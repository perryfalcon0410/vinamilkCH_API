package vn.viettel.sale.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.sale.service.dto.StockCountingExcel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


public class StockCountingFilledExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<StockCountingExcel> stockCountingExcels;
    private CellStyle headerStyle;

    public StockCountingFilledExporter(List<StockCountingExcel> exchangeTransExcelList) {
        this.stockCountingExcels = exchangeTransExcelList;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Stock_Counting_Filled");

        ////////// CUSTOMER HEADER /////////////////////////////
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
        Row customerRow = sheet.createRow(0); // name
        CellStyle customerStyle = workbook.createCellStyle();
        XSSFFont customerHeader = workbook.createFont();
        customerHeader.setBold(true);
        customerHeader.setItalic(true);
        customerHeader.setFontHeight(15);
        customerHeader.setFontName("Times New Roman");
        customerStyle.setFont(customerHeader);

        Row customerAddressRow = sheet.createRow(1); // address
        CellStyle customerAddressStyle = workbook.createCellStyle();
        XSSFFont customerAddressHeader = workbook.createFont();
        customerAddressHeader.setItalic(true);
        customerAddressHeader.setFontHeight(11);
        customerAddressHeader.setFontName("Times New Roman");
        customerAddressStyle.setFont(customerAddressHeader);
        Row customerPhoneRow = sheet.createRow(2);// phone
        createCell(customerRow, 0, "CH GTSP Hải Dương",customerStyle);
        createCell(customerAddressRow, 0, "8 Hoàng Hoa Thám - Hải Dương",customerAddressStyle);
        createCell(customerPhoneRow, 0, "Tel: (84.320) 3 838 399  Fax:",customerAddressStyle);
        ////////////////////////////////////////////////////////

        ////////// COMPANY HEADER /////////////////////////////
        sheet.addMergedRegion(CellRangeAddress.valueOf("J1:Q1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J2:Q2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J3:Q3"));
        Row companyRow = sheet.createRow(0); // name
        Row companyAddressRow = sheet.createRow(1);
        Row companyPhoneRow = sheet.createRow(2);
        createCell(companyRow, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM",customerStyle);
        createCell(companyAddressRow, 9, "8 Hoàng Hoa Thám - Hải Dương",customerAddressStyle);
        createCell(companyPhoneRow, 9, "Tel: (84.320) 3 838 399  Fax:",customerAddressStyle);
        ////////////////////////////////////////////////////////

        sheet.addMergedRegion(CellRangeAddress.valueOf("A5:P5"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:P6"));
        Row header = sheet.createRow(5);
        Row dateRow = sheet.createRow(6);
        Row row = sheet.createRow(8);
        headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(10);
        font.setFontName("Times New Roman");
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontHeight(15);
        fontHeader.setFontName("Times New Roman");
        fontHeader.setBold(true);
        titleStyle.setFont(fontHeader);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        float totalQuantityStock = 0, totalUnitQuantity = 0, totalInventoryQuantity = 0;
        float totalAmount = 0;
        double totalChange = 0;
        for (StockCountingExcel exchange : stockCountingExcels){
            totalQuantityStock = totalQuantityStock + exchange.getStockQuantity();
            totalAmount = totalAmount + exchange.getTotalAmount();
            totalChange = totalChange + exchange.getChangeQuantity();
            totalUnitQuantity = totalUnitQuantity + exchange.getUnitQuantity();
            totalInventoryQuantity = totalInventoryQuantity + exchange.getInventoryQuantity();
        }
        int size = stockCountingExcels.size();
        Row totalRow = sheet.createRow(9 + size + 1);
        CellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont fontTotal = workbook.createFont();
        fontTotal.setFontHeight(10);
        fontTotal.setFontName("Calibri");
        fontTotal.setBold(true);
        totalRowStyle.setFont(fontTotal);

        byte[] rgb = new byte[]{(byte)255, (byte)204, (byte)153};
        XSSFCellStyle totalRowStyleRGB = (XSSFCellStyle)totalRowStyle;
        XSSFColor customColor = new XSSFColor(rgb,null);
        totalRowStyleRGB.setFillForegroundColor(customColor);
        totalRowStyleRGB.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalRowStyleRGB.setBorderBottom(BorderStyle.THIN);
        totalRowStyleRGB.setBorderTop(BorderStyle.THIN);
        totalRowStyleRGB.setBorderLeft(BorderStyle.THIN);
        totalRowStyleRGB.setBorderRight(BorderStyle.THIN);

        createCellTotal(totalRow,4, "Tổng cộng", totalRowStyleRGB);
        createCellTotal(totalRow,5, totalQuantityStock, totalRowStyleRGB);
        createCellTotal(totalRow,7, totalAmount, totalRowStyleRGB);
        createCellTotal(totalRow,9, totalUnitQuantity, totalRowStyleRGB);
        createCellTotal(totalRow,10, totalInventoryQuantity, totalRowStyleRGB);
        createCellTotal(totalRow,11, totalChange, totalRowStyleRGB);

        createCell(header, 0, "KIỂM KÊ HÀNG", titleStyle);
        createCell(dateRow, 0, "Ngày: 22/03/2021", customerAddressStyle);
        createCell(row, 0, "STT", headerStyle);
        createCell(row, 1, "NGÀNH HÀNG", headerStyle);
        createCell(row, 2, "NHÓM SP", headerStyle);
        createCell(row, 3, "MÃ SP", headerStyle);
        createCell(row, 4, "TÊN SP", headerStyle);
        createCell(row, 5, "SL TỒN KHO", headerStyle);
        createCell(row, 6, "GIÁ", headerStyle);
        createCell(row, 7, "THÀNH TIỀN", headerStyle);
        createCell(row, 8, "SL PACKET KIỂM KÊ", headerStyle);
        createCell(row, 9, "SL LẺ KIỂM KÊ", headerStyle);
        createCell(row, 10, "TỔNG SL KIỂM KÊ", headerStyle);
        createCell(row, 11, "CHÊNH LỆCH", headerStyle);
        createCell(row, 12, "ĐVT PACKET", headerStyle);
        createCell(row, 13, "SL QUY ĐỔI", headerStyle);
        createCell(row, 14, "ĐVT LẺ", headerStyle);
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

    private void createCellTotal(Row atRow, int atCell, Object value, CellStyle style) {
        sheet.autoSizeColumn(atCell);
        Cell totalCell = atRow.createCell(atCell);

        if (value instanceof Integer) {
            totalCell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            totalCell.setCellValue((Boolean) value);
        }else if(value instanceof Float) {
            totalCell.setCellValue((Float)value);
        }else if(value instanceof Double) {
            totalCell.setCellValue((Double) value);
        }else if(value instanceof Long) {
            totalCell.setCellValue((Long) value);
        }
        else{
            totalCell.setCellValue((String) value);
        }
        totalCell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 10;
        int stt = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(11);
        font.setFontName("Calibri");
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        for (StockCountingExcel exchange : stockCountingExcels) {
            stt++;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, stt, style);
            createCell(row, columnCount++, exchange.getProductCategory(), style);
            createCell(row, columnCount++, exchange.getProductGroup(), style);
            createCell(row, columnCount++, exchange.getProductCode(), style);
            createCell(row, columnCount++, exchange.getProductName(), style);
            createCell(row, columnCount++, exchange.getStockQuantity(), style);
            createCell(row, columnCount++, exchange.getPrice(), style);
            createCell(row, columnCount++, exchange.getTotalAmount(), style);
            createCell(row, columnCount++, exchange.getPacketQuantity(), style);
            createCell(row, columnCount++, exchange.getUnitQuantity(), style);
            createCell(row, columnCount++, exchange.getInventoryQuantity(), style);
            createCell(row, columnCount++, exchange.getChangeQuantity(), style);
            createCell(row, columnCount++, exchange.getPacketUnit(), style);
            createCell(row, columnCount++, exchange.getConvfact(), style);
            createCell(row, columnCount++, exchange.getUnit(), style);
        }

        float totalQuantityStock = 0, totalAmount = 0;
        double totalChange = 0;
        int size = 0;
        for (StockCountingExcel exchange : stockCountingExcels){
            size++;
            totalQuantityStock = totalQuantityStock + exchange.getStockQuantity();
            totalAmount = totalAmount + exchange.getTotalAmount();

            totalChange = totalChange + exchange.getChangeQuantity();
        }
        Row totalRow = sheet.createRow(9);
        CellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont fontTotal = workbook.createFont();
        fontTotal.setFontHeight(10);
        fontTotal.setFontName("Calibri");
        fontTotal.setBold(true);
        totalRowStyle.setFont(fontTotal);
        byte[] rgb = new byte[]{(byte)255, (byte)204, (byte)153};
        XSSFCellStyle totalRowStyleRGB = (XSSFCellStyle)totalRowStyle;
        XSSFColor customColor = new XSSFColor(rgb,null);
        totalRowStyleRGB.setFillForegroundColor(customColor);
        totalRowStyleRGB.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalRowStyleRGB.setBorderBottom(BorderStyle.THIN);
        totalRowStyleRGB.setBorderTop(BorderStyle.THIN);
        totalRowStyleRGB.setBorderLeft(BorderStyle.THIN);
        totalRowStyleRGB.setBorderRight(BorderStyle.THIN);

        createCellTotal(totalRow,4, "Tổng cộng", totalRowStyleRGB);
        createCellTotal(totalRow,5, totalQuantityStock, totalRowStyleRGB);
        createCellTotal(totalRow,7, totalAmount, totalRowStyleRGB);
        createCellTotal(totalRow,11, totalChange, totalRowStyleRGB);

    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
