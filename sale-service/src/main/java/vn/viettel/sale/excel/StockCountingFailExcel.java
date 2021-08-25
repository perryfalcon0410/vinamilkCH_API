package vn.viettel.sale.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.sale.service.dto.StockCountingExcel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockCountingFailExcel {
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private List<StockCountingExcel> stockCountingExcels;
    private CellStyle headerStyle;
    private LocalDateTime date;

    public StockCountingFailExcel(List<StockCountingExcel> exchangeTransExcelList,  LocalDateTime date) {
        this.stockCountingExcels = exchangeTransExcelList;
        workbook = new SXSSFWorkbook();
        this.date = date;
    }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Stock_Counting_Fail");
        ////////// CUSTOMER HEADER /////////////////////////////
        CellStyle customerStyle = workbook.createCellStyle();
        XSSFFont customerHeader = (XSSFFont) workbook.createFont();
        customerHeader.setBold(true);
        customerHeader.setItalic(true);
        customerHeader.setFontHeight(15);
        customerHeader.setFontName("Times New Roman");
        customerStyle.setFont(customerHeader);
        CellStyle customerAddressStyle = workbook.createCellStyle();
        XSSFFont customerAddressHeader = (XSSFFont) workbook.createFont();
        customerAddressHeader.setItalic(true);
        customerAddressHeader.setFontHeight(11);
        customerAddressHeader.setFontName("Times New Roman");
        customerAddressStyle.setFont(customerAddressHeader);

        ////////// COMPANY HEADER /////////////////////////////
       /* sheet.addMergedRegion(CellRangeAddress.valueOf("A6:P6"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A7:P7"));*/
        Row header = sheet.createRow(0);
        Row dateRow = sheet.createRow(1);
        Row row = sheet.createRow(3);
        headerStyle = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
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
        XSSFFont fontHeader = (XSSFFont) workbook.createFont();
        fontHeader.setFontHeight(15);
        fontHeader.setFontName("Times New Roman");
        fontHeader.setBold(true);
        titleStyle.setFont(fontHeader);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        double totalQuantityStock = 0;
        double totalAmount = 0;
        double totalChange = 0;
        for (int i = 0; i<stockCountingExcels.size(); i++){

            StockCountingExcel exchange = stockCountingExcels.get(i);
            totalQuantityStock = totalQuantityStock + (exchange.getStockQuantity()==null?0:exchange.getStockQuantity());
            totalAmount = totalAmount + (exchange.getTotalAmount()==null?0:exchange.getTotalAmount());
            totalChange = totalChange + (exchange.getChangeQuantity()==null?0:exchange.getChangeQuantity());
        }
        int size = stockCountingExcels.size();
        Row totalRowDown = sheet.createRow(10 + size);
        CellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont fontTotal = (XSSFFont) workbook.createFont();
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

        CellStyle totalRowStyleRGB2 = totalRowStyleRGB;
        DataFormat dataFormat = workbook.createDataFormat();
        totalRowStyleRGB2.setDataFormat(dataFormat.getFormat("#,###"));

        ExcelPoiUtils.createCell(header, 0, "KIỂM KÊ HÀNG", titleStyle);
        ExcelPoiUtils.createCell(dateRow, 0, DateUtils.formatDate2StringDate(date), customerAddressStyle);
        ExcelPoiUtils.createCell(row, 0, "STT", headerStyle);
        ExcelPoiUtils.createCell(row, 1, "NGÀNH HÀNG", headerStyle);
        ExcelPoiUtils.createCell(row, 2, "NHÓM SP", headerStyle);
        ExcelPoiUtils.createCell(row, 3, "MÃ SP", headerStyle);
        ExcelPoiUtils.createCell(row, 4, "TÊN SP", headerStyle);
        ExcelPoiUtils.createCell(row, 5, "SL TỒN KHO", headerStyle);
        ExcelPoiUtils.createCell(row, 6, "GIÁ", headerStyle);
        ExcelPoiUtils.createCell(row, 7, "THÀNH TIỀN", headerStyle);
        ExcelPoiUtils.createCell(row, 8, "SL PACKET KIỂM KÊ", headerStyle);
        ExcelPoiUtils.createCell(row, 9, "SL LẺ KIỂM KÊ", headerStyle);
        ExcelPoiUtils.createCell(row, 10, "TỔNG SL KIỂM KÊ", headerStyle);
        ExcelPoiUtils.createCell(row, 11, "CHÊNH LỆCH", headerStyle);
        ExcelPoiUtils.createCell(row, 12, "ĐVT PACKET", headerStyle);
        ExcelPoiUtils.createCell(row, 13, "SL QUY ĐỔI", headerStyle);
        ExcelPoiUtils.createCell(row, 14, "ĐVT LẺ", headerStyle);
        ExcelPoiUtils.createCell(row, 15, "Lỗi", headerStyle);

    }






    private void writeDataLines() {
        int rowCount =4;
        int stt = 0;
        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontHeight(11);
        font.setFontName("Calibri");
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(dataFormat.getFormat("text"));


        CellStyle dataStyle2 = style;

        dataStyle2.setDataFormat(dataFormat.getFormat("#,###"));


        for (int i = 0; i<stockCountingExcels.size(); i++) {
            stt++;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            StockCountingExcel exchange = stockCountingExcels.get(i);
            ExcelPoiUtils.createCell(row, columnCount++, stt, style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductCategory(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductGroup(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductCode(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductName(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getStockQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getPrice(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getTotalAmount(), dataStyle2);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getPacketQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getUnitQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getInventoryQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getChangeQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getPacketUnit(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getConvfact(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getUnit(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getError(), style);
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet, 15);
    }
    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        IOUtils.closeQuietly(out);
        return response;
    }
}
