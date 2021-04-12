package vn.viettel.sale.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

        Row header = sheet.createRow(0);
        Row row = sheet.createRow(1);
        headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(10);
        font.setFontName("Times New Roman");
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontHeight((short) 20);
        fontHeader.setFontName("Times New Roman");
        titleStyle.setFont(fontHeader);

        titleStyle.setAlignment(HorizontalAlignment.CENTER);
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
        Row totalRow = sheet.createRow(2 + size + 1);
        CellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont fontTotal = workbook.createFont();
        fontTotal.setFontHeight(10);
        fontTotal.setFontName("Calibri");
        fontTotal.setBold(true);
        totalRowStyle.setFont(fontTotal);
        totalRowStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        totalRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalRowStyle.setBorderBottom(BorderStyle.THIN);
        totalRowStyle.setBorderTop(BorderStyle.THIN);
        totalRowStyle.setBorderLeft(BorderStyle.THIN);
        totalRowStyle.setBorderRight(BorderStyle.THIN);
        totalRowStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        totalRowStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        totalRowStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        totalRowStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        createCell(header, 0, "KIỂM KÊ HÀNG", titleStyle);
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

        createCellTotal(totalRow,4, "Tổng cộng", totalRowStyle);
        createCellTotal(totalRow,5, totalQuantityStock, totalRowStyle);
        createCellTotal(totalRow,7, totalAmount, totalRowStyle);
        createCellTotal(totalRow,9, totalUnitQuantity, totalRowStyle);
        createCellTotal(totalRow,10, totalInventoryQuantity, totalRowStyle);
        createCellTotal(totalRow,11, totalChange, totalRowStyle);

        header.setHeight((short) 800);
        row.setHeight((short) 650);
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
        int rowCount = 3;
        int stt = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight((short) 11);
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

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
        Row totalRow = sheet.createRow(2);
        CellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont fontTotal = workbook.createFont();
        fontTotal.setFontHeight(10);
        fontTotal.setFontName("Calibri");
        fontTotal.setBold(true);
        totalRowStyle.setFont(fontTotal);
        totalRowStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        totalRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalRowStyle.setBorderBottom(BorderStyle.THIN);
        totalRowStyle.setBorderTop(BorderStyle.THIN);
        totalRowStyle.setBorderLeft(BorderStyle.THIN);
        totalRowStyle.setBorderRight(BorderStyle.THIN);
        totalRowStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        totalRowStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        totalRowStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        totalRowStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        createCellTotal(totalRow,4, "Tổng cộng", totalRowStyle);
        createCellTotal(totalRow,5, totalQuantityStock, totalRowStyle);
        createCellTotal(totalRow,7, totalAmount, totalRowStyle);
        createCellTotal(totalRow,11, totalChange, totalRowStyle);

    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
