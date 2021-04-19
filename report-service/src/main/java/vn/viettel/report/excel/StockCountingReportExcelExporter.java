package vn.viettel.report.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.report.service.dto.StockCountingReportDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class StockCountingReportExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<StockCountingReportDTO> stockCountingList;
    private Shop shop;

    public StockCountingReportExcelExporter(List<StockCountingReportDTO> stockCountingList, Shop shop) {
        this.stockCountingList = stockCountingList;
        this.shop = shop;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Sheet1");

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

//        styleHeader.setAlignment(HorizontalAlignment.CENTER);
//        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);

        createCell(header, 0, shop.getShopName(), styleHeader);
        createCell(row, 0, "STT", style);
        createCell(row, 1, "NGÀNH HÀNG", style);
        createCell(row, 2, "MÃ SẢN PHẨM", style);
        createCell(row, 3, "TÊN SẢN PHÂM", style);
        createCell(row, 4, "SỐ LƯỢNG", style);
        createCell(row, 5, "SL PACKET", style);
        createCell(row, 6, "SL LẺ", style);
        createCell(row, 7, "GIÁ", style);
        createCell(row, 8, "THÀNH TIỀN", style);
        createCell(row, 9, "ĐVT PACKET", style);
        createCell(row, 10, "ĐVT LẺ", style);
        createCell(row, 11, "CỬA HÀNG", style);
        createCell(row, 12, "CHUỖI CỬA HÀNG", style);
        createCell(row, 13, "NHÓM SẢN PHẢM", style);
        createCell(row, 14, "TỔNG MIN", style);
        createCell(row, 15, "TỔNG MAX", style);
        createCell(row, 16, "CẢNH BÁO", style);

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

        for (StockCountingReportDTO stockCounting : stockCountingList) {
            stt++;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, stt, style);
            createCell(row, columnCount++, stockCounting.getProductCategory(), style);
            createCell(row, columnCount++, stockCounting.getProductCode(), style);
            createCell(row, columnCount++, stockCounting.getProductName(), style);
            createCell(row, columnCount++, stockCounting.getStockQuantity().toString() , style);
            createCell(row, columnCount++, stockCounting.getPacketQuantity(), style);
            createCell(row, columnCount++, stockCounting.getUnitQuantity(), style);
            createCell(row, columnCount++, stockCounting.getPrice(), style);
            createCell(row, columnCount++, stockCounting.getTotalAmount(), style);
            createCell(row, columnCount++, stockCounting.getPacketUnit(), style);
            createCell(row, columnCount++, stockCounting.getUnit(), style);
            createCell(row, columnCount++, stockCounting.getShop(), style);
            createCell(row, columnCount++, stockCounting.getShopType(), style);
            createCell(row, columnCount++, stockCounting.getProductGroup(), style);
            createCell(row, columnCount++, stockCounting.getMinInventory(), style);
            createCell(row, columnCount++, stockCounting.getMaxInventory(), style);
            createCell(row, columnCount++, "No Warning", style);
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
