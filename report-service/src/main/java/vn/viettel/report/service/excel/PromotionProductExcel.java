package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PromotionProductExcel {
    private static final String FONT_NAME= "Times New Roman";

    private XSSFWorkbook workbook;
    private XSSFSheet sheet1;
    private XSSFSheet sheet2;
    private XSSFSheet sheet3;

    private ShopDTO shopDTO;

    private XSSFCellStyle styleTableHeader;
    private XSSFCellStyle styleCellTotalTable;

    public  PromotionProductExcel(ShopDTO shopDTO) {
        this.shopDTO = shopDTO;
        workbook = new XSSFWorkbook();
        this.styleTableHeader = getTableHeaderStyle();
        this.styleCellTotalTable = getTableTotalHeaderStyle();
    }

    private void writeHeaderLine()  {

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setItalic(true);
        font.setFontHeight(15);
        font.setFontName(FONT_NAME);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = workbook.createFont();
        font1.setBold(false);
        font1.setItalic(true);
        font1.setFontHeight(11);
        font1.setFontName(FONT_NAME);
        style1.setFont(font1);
        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName(FONT_NAME);
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        List<XSSFSheet> sheets = new ArrayList<>();
            sheet1 = workbook.createSheet("KM_ChiTiet");
            sheet2 = workbook.createSheet("KM_TheoNgay");
            sheet3 = workbook.createSheet("KM_TheoSP");
        sheets.add(sheet1);
        sheets.add(sheet2);
        sheets.add(sheet3);

        for(XSSFSheet sheet: sheets) {
            Row row = sheet.createRow(0);
            Row row1 = sheet.createRow(1);
            Row row2 = sheet.createRow(2);
            Row row5 = sheet.createRow(5);
            Row row7 = sheet.createRow(7);

            row.setRowStyle(style);
            row2.setRowStyle(style);
            row1.setRowStyle(style1);
            row2.setRowStyle(style1);
            row5.setRowStyle(style2);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("H1:M1"));
            createCell(sheet, row, 0, shopDTO.getShopName(), style);
            createCell(sheet, row, 7, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
            createCell(sheet, row1, 0, shopDTO.getAddress(), style1);
            createCell(sheet, row1, 7, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style1);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
            createCell(sheet, row2, 7, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style1);
            createCell(sheet, row2, 0,"Tel: " + shopDTO.getMobiPhone() + " Fax: " + shopDTO.getFax(), style1);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:N6"));
            createCell(sheet, row5, 0, "BÁO CÁO HÀNG KHUYẾN MÃI", style2);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A8:N8"));
            createCell(sheet, row7, 0, "TỪ NGÀY: 01/04/2021   ĐẾN NGÀY: 23/04/2021", style1);

        }

    }

    private void createTableSheet1() {
        Row rowHeader = sheet1.createRow(8);
        createCell(sheet1, rowHeader, 0, "STT", styleTableHeader);
        createCell(sheet1, rowHeader, 1, "NGÀY BÁN", styleTableHeader);
        createCell(sheet1, rowHeader, 2, "NGÀNH HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 3, "MÃ HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 4, "HÓA ĐƠN", styleTableHeader);
        createCell(sheet1, rowHeader, 5, "SL", styleTableHeader);
        createCell(sheet1, rowHeader, 6, "GIÁ", styleTableHeader);
        createCell(sheet1, rowHeader, 7, "THÀNH TIẾN", styleTableHeader);
        createCell(sheet1, rowHeader, 8, "BARCODE", styleTableHeader);
        createCell(sheet1, rowHeader, 9, "TÊN HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 10, "ĐVT", styleTableHeader);
        createCell(sheet1, rowHeader, 11, "MÃ CTKM", styleTableHeader);
        createCell(sheet1, rowHeader, 12, "SỐ ĐƠN ONLINE", styleTableHeader);
        createCell(sheet1, rowHeader, 13, "LOẠI", styleTableHeader);

        Row rowTotalHearder = sheet1.createRow(9);
        createCell(sheet1, rowTotalHearder, 5, 4, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 6, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 7, 810000, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 8, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 9, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 10, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 11, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 12, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 13, null, styleCellTotalTable);
    }

    private void createTableSheet2() {
        Row rowValues = sheet2.createRow(8);
        createCell(sheet2, rowValues, 0, "STT", styleTableHeader);
        createCell(sheet2, rowValues, 1, "NGÀY BÁN", styleTableHeader);
        createCell(sheet2, rowValues, 2, "NGÀNH HÀNG", styleTableHeader);
        createCell(sheet2, rowValues, 3, "MÃ HÀNG", styleTableHeader);
        createCell(sheet2, rowValues, 4, "BAR_CODE", styleTableHeader);
        createCell(sheet2, rowValues, 5, "TÊN HÀNG", styleTableHeader);
        createCell(sheet2, rowValues, 6, "TÊN IN HĐ", styleTableHeader);
        createCell(sheet2, rowValues, 7, "ĐVT", styleTableHeader);
        createCell(sheet2, rowValues, 8, "SL", styleTableHeader);
        createCell(sheet2, rowValues, 9, "GIÁ", styleTableHeader);
        createCell(sheet2, rowValues, 10, "THÀNH TIỀN", styleTableHeader);

        Row rowTotalHearder = sheet2.createRow(9);
        createCell(sheet1, rowTotalHearder, 5, "Tổng:", styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 6, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 7, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 8, 4, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 9, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 10, 81900, styleCellTotalTable);

    }

    private void createTableSheet3() {
        Row rowValues = sheet3.createRow(8);
        createCell(sheet3, rowValues, 0, "STT", styleTableHeader);
        createCell(sheet3, rowValues, 1, "NGÀY HÀNG", styleTableHeader);
        createCell(sheet3, rowValues, 2, "MÃ HÀNG", styleTableHeader);
        createCell(sheet3, rowValues, 3, "BAR_CODE", styleTableHeader);
        createCell(sheet3, rowValues, 4, "TÊN HÀNG", styleTableHeader);
        createCell(sheet3, rowValues, 5, "TÊN IN HĐ", styleTableHeader);
        createCell(sheet3, rowValues, 6, "ĐVT", styleTableHeader);
        createCell(sheet3, rowValues, 7, "KHUYẾN MÃI", styleTableHeader);
        createCell(sheet3, rowValues, 8, "GIÁ", styleTableHeader);
        createCell(sheet3, rowValues, 9, "THÀNH TIỀN", styleTableHeader);

        Row rowTotalHearder = sheet3.createRow(9);
        createCell(sheet1, rowTotalHearder, 5, "Tổng:", styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 6, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 7, 4, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 8, null, styleCellTotalTable);
        createCell(sheet1, rowTotalHearder, 9, 81900, styleCellTotalTable);
    }

    public XSSFCellStyle getTableHeaderStyle() {
        CellStyle styleHeader1 = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setBold(true);
        fontHeader.setItalic(false);
        fontHeader.setFontHeight(10);
        fontHeader.setFontName(FONT_NAME);
        styleHeader1.setFont(fontHeader);
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

        return styleHeader;
    }

    public XSSFCellStyle getTableTotalHeaderStyle() {
        CellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont fontTotal = workbook.createFont();
        fontTotal.setFontHeight(10);
        fontTotal.setFontName(FONT_NAME);
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

        return totalRowStyleRGB;
    }

    private void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {
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

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        this.createTableSheet2();
        this.createTableSheet3();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
