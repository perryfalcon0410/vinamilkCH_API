package vn.viettel.sale.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.sale.service.dto.ExchangeTransExcel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExchangeTransExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ExchangeTransExcel> exchangeTransExcelList;

    public ExchangeTransExporter(List<ExchangeTransExcel> exchangeTransExcelList) {
        this.exchangeTransExcelList = exchangeTransExcelList;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Exchange_Trans");

        Row header = sheet.createRow(0);
        Row row = sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.NO_FILL);

        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        row.setRowStyle(style);

        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontHeight(20);
        fontHeader.setFontName("Times New Roman");
        styleHeader.setFont(fontHeader);

        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        float totalQuantityStock = 0, totalChange = 0, totalUnitQuantity = 0, totalInventoryQuantity = 0;
        double totalPrice = 0;
        for (ExchangeTransExcel exchange : exchangeTransExcelList){
            totalQuantityStock = totalQuantityStock + exchange.getStockQuantity();
            totalPrice = totalPrice + exchange.getPrice();
            totalChange = totalChange + exchange.getChangeQuantity();
            totalUnitQuantity = totalUnitQuantity + exchange.getUnitQuantity();
            totalInventoryQuantity = totalInventoryQuantity + exchange.getInventoryQuantity();
        }

        createCell(header, 0, "KIỂM KÊ HÀNG", styleHeader);

        createCell(row,6, "Tổng cộng", style);
        createCell(row,5, totalQuantityStock, style);
        createCell(row,7, totalPrice, style);
        createCell(row,11, totalPrice, style);

        createCell(row, 0, "STT", style);
        createCell(row, 1, "NGÀNH HÀNG", style);
        createCell(row, 2, "NHÓM SP", style);
        createCell(row, 3, "MÃ SP", style);
        createCell(row, 4, "TÊN SP", style);
        createCell(row, 5, "SL TỒN KHO", style);
        createCell(row, 6, "GIÁ", style);
        createCell(row, 7, "THÀNH TIỀN", style);
        createCell(row, 8, "SL PACKET KIỂM KÊ", style);
        createCell(row, 9, "SL LẺ KIỂM KÊ", style);
        createCell(row, 10, "TỔNG SL KIỂM KÊ", style);
        createCell(row, 11, "CHÊNH LỆCH", style);
        createCell(row, 12, "ĐVT PACKET", style);
        createCell(row, 13, "SL QUY ĐỔI", style);
        createCell(row, 14, "ĐVT LẺ", style);

        createCell(row,6, "Tổng cộng", style);
        createCell(row,5, totalQuantityStock, style);
        createCell(row,7, totalPrice, style);
        createCell(row,9, totalUnitQuantity, style);
        createCell(row,10, totalInventoryQuantity, style);
        createCell(row,11, totalChange, style);

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
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 2;
        int stt = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);

        for (ExchangeTransExcel exchange : exchangeTransExcelList) {
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
