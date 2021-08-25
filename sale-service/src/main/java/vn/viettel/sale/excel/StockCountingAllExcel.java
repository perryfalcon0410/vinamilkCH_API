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
import vn.viettel.sale.service.dto.StockCountingDetailDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.List;

public class StockCountingAllExcel {
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private List<StockCountingDetailDTO> stockCountingExcels;
    private CellStyle headerStyle;
    private ShopDTO shop;
    private ShopDTO parentShop;
    private LocalDateTime date;
    public StockCountingAllExcel(List<StockCountingDetailDTO> exchangeTransExcelList, ShopDTO shop, ShopDTO parentShop, LocalDateTime date) {
        this.stockCountingExcels = exchangeTransExcelList;
        workbook = new SXSSFWorkbook();
        this.shop = shop;
        this.date = date;
        this.parentShop = parentShop;
    }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Stock_Counting_All");
        ////////// CUSTOMER HEADER /////////////////////////////
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J1:Q1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J2:Q2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J3:Q3"));
        Row customerRow = sheet.createRow(0); // name
        Row customerAddressRow = sheet.createRow(1); // address
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
        Row customerPhoneRow = sheet.createRow(2);// phone

        ExcelPoiUtils.createCell(customerRow, 0, shop.getShopName(),customerStyle);
        ExcelPoiUtils.createCell(customerAddressRow, 0, shop.getAddress(),customerAddressStyle);
        ExcelPoiUtils.createCell(customerPhoneRow, 0, "Tel: " + shop.getMobiPhone()!=null?shop.getMobiPhone():"" + " Fax: " + shop.getFax()!=null?shop.getFax():"",customerAddressStyle);

        if(parentShop != null) {
            ExcelPoiUtils.createCell(customerRow, 9, parentShop.getShopName(),customerStyle);
            ExcelPoiUtils.createCell(customerAddressRow, 9, parentShop.getAddress() ,customerAddressStyle);
            ExcelPoiUtils.createCell(customerPhoneRow, 9, "Tel: " + parentShop.getMobiPhone()!=null?parentShop.getMobiPhone():"" + " Fax: " + parentShop.getFax()!=null?parentShop.getFax():"",customerAddressStyle);
        }

        ////////// COMPANY HEADER /////////////////////////////
        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:P6"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A7:P7"));
        Row header = sheet.createRow(5);
        Row dateRow = sheet.createRow(6);
        Row row = sheet.createRow(8);
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
        double totalQuantityStock = 0, totalUnitQuantity = 0, totalInventoryQuantity = 0;
        double totalAmount = 0;
        double totalChange = 0;
        for (StockCountingDetailDTO exchange : stockCountingExcels){
            totalQuantityStock = totalQuantityStock + exchange.getStockQuantity();
            totalAmount = totalAmount + exchange.getTotalAmount();
            totalChange = totalChange + exchange.getChangeQuantity();
            totalUnitQuantity = totalUnitQuantity + exchange.getUnitQuantity();
            totalInventoryQuantity = totalInventoryQuantity + exchange.getInventoryQuantity();
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

        ExcelPoiUtils.createCell(totalRowDown,4, "Tổng cộng", totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,5, totalQuantityStock, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,7, totalAmount, totalRowStyleRGB2);
        ExcelPoiUtils.createCell(totalRowDown,9, totalUnitQuantity, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,10, totalInventoryQuantity, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,11, totalChange, totalRowStyleRGB);
        ///// FILLED ROW ///////////
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        ExcelPoiUtils.createCell(totalRowDown,0, null, style);
        ExcelPoiUtils.createCell(totalRowDown,1, null, style);
        ExcelPoiUtils.createCell(totalRowDown,2, null, style);
        ExcelPoiUtils.createCell(totalRowDown,3, null, style);
        ExcelPoiUtils.createCell(totalRowDown,6, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,8, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,12, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,13, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowDown,14, null, totalRowStyleRGB);

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

        Row totalRowUp = sheet.createRow(9);
        ExcelPoiUtils.createCell(totalRowUp,4, "Tổng cộng", totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,5, totalQuantityStock, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,7, totalAmount, totalRowStyleRGB2);
        ExcelPoiUtils.createCell(totalRowUp,11, totalChange, totalRowStyleRGB);
        /// FILLED ROW /////////////////////
        ExcelPoiUtils.createCell(totalRowUp,0, null, style);
        ExcelPoiUtils.createCell(totalRowUp,1, null, style);
        ExcelPoiUtils.createCell(totalRowUp,2, null, style);
        ExcelPoiUtils.createCell(totalRowUp,3, null, style);
        ExcelPoiUtils.createCell(totalRowUp,6, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,8, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,9, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,10, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,12, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,13, null, totalRowStyleRGB);
        ExcelPoiUtils.createCell(totalRowUp,14, null, totalRowStyleRGB);
    }

    private void writeDataLines() {
        int rowCount = 10;
        int stt = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontHeight(11);
        font.setFontName("Calibri");
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyle2 = style;
        DataFormat dataFormat = workbook.createDataFormat();
        dataStyle2.setDataFormat(dataFormat.getFormat("#,###"));

        for (StockCountingDetailDTO exchange : stockCountingExcels) {
            stt++;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            ExcelPoiUtils.createCell(row, columnCount++, stt, style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductCategory(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductGroup(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductCode(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getProductName(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getStockQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getPrice(), dataStyle2);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getTotalAmount(), dataStyle2);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getPacketQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getUnitQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getInventoryQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getChangeQuantity(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getPacketUnit(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getConvfact(), style);
            ExcelPoiUtils.createCell(row, columnCount++, exchange.getUnit(), style);
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet, 14);
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
