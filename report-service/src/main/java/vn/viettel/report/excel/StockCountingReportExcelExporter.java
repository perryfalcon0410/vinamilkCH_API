package vn.viettel.report.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
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

    private void writeHeaderLine()  {
        sheet = workbook.createSheet("Tồn Kho");
        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        Row row5 = sheet.createRow(5);
        Row rowValues = sheet.createRow(8);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setItalic(true);
        font.setFontHeight(15);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row.setRowStyle(style);
        row2.setRowStyle(style);

        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName("Times New Roman");
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row5.setRowStyle(style2);

        CellStyle styleHeader1 = workbook.createCellStyle();
        XSSFFont fontheader = workbook.createFont();
        fontheader.setBold(true);
        fontheader.setItalic(false);
        fontheader.setFontHeight(10);
        fontheader.setFontName("Times New Roman");
        styleHeader1.setFont(fontheader);
        byte[] rgb = new byte[]{(byte)192, (byte)192, (byte)192};
        XSSFCellStyle styleHeader = (XSSFCellStyle)styleHeader1;
        XSSFColor colorHeader = new XSSFColor(rgb,null);
        styleHeader.setFillForegroundColor(colorHeader);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);

        int rowf = 0,rowt = 0;
        int colf=0,colt = 6;
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
        sheet.addMergedRegion(new CellRangeAddress(rowf,rowt,colf+7,colt+6));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:G6"));

        createCell(row, rowf, shop.getShopName(), style);
        createCell(row, 7, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);
        createCell(row1, 0, shop.getAddress(), style);
        createCell(row1, 7, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style);
        createCell(row2, 0, shop.getPhone(), style);
        createCell(row2, 7, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style);
        createCell(row5, 0, "DANH SÁCH DỮ LIỆU", style2);

        createCell(rowValues, 0, "STT", styleHeader);
        createCell(rowValues, 1, "NGÀNH HÀNG", styleHeader);
        createCell(rowValues, 2, "MÃ SẢN PHẨM", styleHeader);
        createCell(rowValues, 3, "TÊN SẢN PHẨM", styleHeader);
        createCell(rowValues, 4, "SỐ LƯỢNG", styleHeader);
        createCell(rowValues, 5, "SL PACKET", styleHeader);
        createCell(rowValues, 6, "SL LẺ", styleHeader);
        createCell(rowValues, 6, "GIÁ", styleHeader);
        createCell(rowValues, 6, "THÀNH TIỀN", styleHeader);
        createCell(rowValues, 6, "ĐVT PACKET", styleHeader);
        createCell(rowValues, 6, "ĐVT LẺ", styleHeader);
        createCell(rowValues, 6, "CỬA HÀNG", styleHeader);
        createCell(rowValues, 6, "CHUỖI CỬA HÀNG", styleHeader);
        createCell(rowValues, 6, "NHÓM SẢN PHẨM", styleHeader);
        createCell(rowValues, 6, "TỒN MIN", styleHeader);
        createCell(rowValues, 6, "TỒN MAX", styleHeader);
        createCell(rowValues, 6, "CẢNH BÁO", styleHeader);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        }
        else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 9;

        CellStyle styleValues = workbook.createCellStyle();
        XSSFFont fontValues = workbook.createFont();
        fontValues.setBold(false);
        fontValues.setItalic(false);
        fontValues.setFontHeight(9);
        fontValues.setFontName("Times New Roman");
        styleValues.setFont(fontValues);
        styleValues.setBorderTop(BorderStyle.THIN);
        styleValues.setBorderBottom(BorderStyle.THIN);
        styleValues.setBorderLeft(BorderStyle.THIN);
        styleValues.setBorderRight(BorderStyle.THIN);

        for (StockCountingReportDTO stockCounting : stockCountingList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, stockCounting.getProductCategory(), styleValues);
            createCell(row, columnCount++, stockCounting.getProductCode(), styleValues);
            createCell(row, columnCount++, stockCounting.getProductName(), styleValues);
            createCell(row, columnCount++, stockCounting.getProductName(), styleValues);
            createCell(row, columnCount++, stockCounting.getStockQuantity(), styleValues);
            createCell(row, columnCount++, stockCounting.getPacketQuantity(), styleValues);
            createCell(row, columnCount++, stockCounting.getUnitQuantity(), styleValues);
            createCell(row, columnCount++, stockCounting.getPrice(), styleValues);
            createCell(row, columnCount++, stockCounting.getPrice() *stockCounting.getStockQuantity(), styleValues);
            createCell(row, columnCount++, stockCounting.getPacketUnit(), styleValues);
            createCell(row, columnCount++, stockCounting.getUnit(), styleValues);
            createCell(row, columnCount++, stockCounting.getShop(), styleValues);
            createCell(row, columnCount++, stockCounting.getShopType(), styleValues);
            createCell(row, columnCount++, stockCounting.getProductGroup(), styleValues);
            createCell(row, columnCount++, stockCounting.getMinInventory(), styleValues);
            createCell(row, columnCount++, stockCounting.getMaxInventory(), styleValues);
            createCell(row, columnCount++, stockCounting.getWarning(), styleValues);
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
