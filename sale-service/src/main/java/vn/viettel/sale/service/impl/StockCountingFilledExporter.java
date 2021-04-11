package vn.viettel.sale.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.sale.service.dto.StockCountingExcel;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


public class StockCountingFilledExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<StockCountingExcel> stockCountingExcels;
    private XSSFCellStyle headerStyle;

    public StockCountingFilledExporter(List<StockCountingExcel> exchangeTransExcelList) {
        this.stockCountingExcels = exchangeTransExcelList;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Stock_Counting_Filled");

        Row header = sheet.createRow(0);
        Row row = sheet.createRow(1);
        headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        XSSFColor headerRowColor = new XSSFColor(Color.gray);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(headerRowColor);
        headerStyle.setFillBackgroundColor(headerRowColor);
        headerStyle.setFillPattern(FillPatternType.NO_FILL);

        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        row.setRowStyle(headerStyle);

        XSSFCellStyle titleStyle = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontHeight(20);
        fontHeader.setFontName("Times New Roman");
        titleStyle.setFont(fontHeader);

        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        float totalQuantityStock = 0, totalChange = 0, totalUnitQuantity = 0, totalInventoryQuantity = 0;
        double totalAmount = 0;
        int size = 0;
        for (StockCountingExcel exchange : stockCountingExcels){
            size++;
            totalQuantityStock = totalQuantityStock + exchange.getStockQuantity();
            totalAmount = totalAmount + exchange.getTotalAmount();

            totalChange = totalChange + exchange.getChangeQuantity();
            totalUnitQuantity = totalUnitQuantity + exchange.getUnitQuantity();
            totalInventoryQuantity = totalInventoryQuantity + exchange.getInventoryQuantity();
        }

        XSSFCellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont fontTotal = workbook.createFont();
        fontTotal.setFontHeight(10);
        fontTotal.setFontName("Calibri");
        totalRowStyle.setFont(fontTotal);
        XSSFColor totalRowColor = new XSSFColor(Color.orange);
        totalRowStyle.setFillBackgroundColor(totalRowColor);

        createCell(header, 0, "KIỂM KÊ HÀNG", titleStyle);

        createCellTotal(9,4, "Tổng cộng", totalRowStyle);
        createCellTotal(9,5, totalQuantityStock, totalRowStyle);
        createCellTotal(9,7, totalAmount, totalRowStyle);
        createCellTotal(9,11, totalChange, totalRowStyle);

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

        createCellTotal(size - 1,4, "Tổng cộng", totalRowStyle);
        createCellTotal(size - 1,5, totalQuantityStock, totalRowStyle);
        createCellTotal(size - 1,7, totalAmount, totalRowStyle);
        createCellTotal(size - 1,9, totalUnitQuantity, totalRowStyle);
        createCellTotal(size - 1,10, totalUnitQuantity, totalRowStyle);
        createCellTotal(size - 1,11, totalChange, totalRowStyle);

        header.setHeight((short) 800);
        row.setHeight((short) 650);
    }

    private void createCell(Row row, int columnCount, Object value, XSSFCellStyle style) {
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
        }
        else{
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void createCellTotal(int atRow, int atCell, Object value, XSSFCellStyle style) {
        Row totalRow = sheet.createRow(atRow);
        Cell totalCell = totalRow.createCell(atCell);

        if (value instanceof Integer) {
            totalCell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            totalCell.setCellValue((Boolean) value);
        }else if(value instanceof Float) {
            totalCell.setCellValue((Float)value);
        }else if(value instanceof Double) {
            totalCell.setCellValue((Double) value);
        }
        else{
            totalCell.setCellValue((String) value);
        }
        totalRow.setRowStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 2;
        int stt = 0;

        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);

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
    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
