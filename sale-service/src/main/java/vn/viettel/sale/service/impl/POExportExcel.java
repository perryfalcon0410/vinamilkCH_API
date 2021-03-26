/*
package vn.viettel.sale.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class POExportExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFSheet sheet2;
    private List<SoConfirmDTO> soConfirms;
    private List<SoConfirmDTO> soConfirms2;
    public POExportExcel( List<SoConfirmDTO> soConfirms,List<SoConfirmDTO> soConfirms2) {
        this.soConfirms = soConfirms;
        this.soConfirms2 = soConfirms2;
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine()  {
        sheet = workbook.createSheet("Sản phẩm");
        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        Row row5 = sheet.createRow(5);
        Row rowValues = sheet.createRow(8);

        ////////////////////////////////////////////////////////////////////
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
        //////////////////////////////////////////////////////////////////
        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = workbook.createFont();
        font1.setBold(false);
        font1.setItalic(true);
        font1.setFontHeight(11);
        font1.setFontName("Times New Roman");
        style1.setFont(font1);
        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row1.setRowStyle(style1);
        row2.setRowStyle(style1);
        //////////////////////////////////////////////////////////////////
        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName("Times New Roman");
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row5.setRowStyle(style2);
        //////////////////////////////////////////////////////////////////
        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontheader = workbook.createFont();
        fontheader.setBold(true);
        fontheader.setItalic(false);
        fontheader.setFontHeight(10);
        fontheader.setFontName("Times New Roman");
        styleHeader.setFont(fontheader);
        styleHeader.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        //////////////////////////////////////////////////////////////////////////////
        int rowf = 0,rowt = 0;
        int colf=0,colt = 6;
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
        sheet.addMergedRegion(new CellRangeAddress(rowf,rowt,colf+7,colt+6));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:G6"));
        createCell(row, rowf, "CH GTSP Hải Dương", style);
        createCell(row, 7, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);
        createCell(row1, 0, "8 Hoàng Hoa Thám - Hải Dương", style1);
        createCell(row1, 7, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style1);
        createCell(row2, 0, "Tel: (84.320) 3 838 399  Fax:", style1);
        createCell(row2, 7, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style1);
        createCell(row5, 0, "DANH SÁCH DỮ LIỆU", style2);
        ////////////////////////////////////////////////////////////////////////////////////////////
        createCell(rowValues, 0, "STT", styleHeader);
        createCell(rowValues, 1, "SO NO", styleHeader);
        createCell(rowValues, 2, "MÃ SẢN PHẨM", styleHeader);
        createCell(rowValues, 3, "TÊN SẢN PHẨM", styleHeader);
        createCell(rowValues, 4, "GIÁ", styleHeader);
        createCell(rowValues, 5, "SỐ LƯỢNG", styleHeader);
        createCell(rowValues, 6, "THÀNH TIỀN", styleHeader);
        //////////////////////////////////////////////////////////////////////////////
        // SHEET 2
        ////////////////////////////////////////////////////////////////////////////
        sheet2 = workbook.createSheet("Hàng khuyến mãi");
        Row row_ = sheet2.createRow(0);
        Row row_1 = sheet2.createRow(1);
        Row row_2 = sheet2.createRow(2);
        Row row_5 = sheet2.createRow(5);
        Row row_Values = sheet2.createRow(8);
        int rowf2 = 0,rowt2 = 0;
        int colf2=0,colt2 = 6;
        sheet2.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
        sheet2.addMergedRegion(new CellRangeAddress(rowf,rowt2,colf2+7,colt2+6));
        sheet2.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
        sheet2.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
        sheet2.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
        sheet2.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
        sheet2.addMergedRegion(CellRangeAddress.valueOf("A6:G6"));
        createCell_(row_, 0, "CH GTSP Hải Dương", style);
        createCell_(row_, 7, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);
        createCell_(row_1, 0, "8 Hoàng Hoa Thám - Hải Dương", style1);
        createCell_(row_1, 7, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style1);
        createCell_(row_2, 0, "Tel: (84.320) 3 838 399  Fax:", style1);
        createCell_(row_2, 7, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style1);
        createCell_(row_5, 0, "DANH SÁCH DỮ LIỆU", style2);
        ////////////////////////////////////////////////////////////////////////////////////////////
        createCell_(row_Values, 0, "STT", styleHeader);
        createCell_(row_Values, 1, "SO NO", styleHeader);
        createCell_(row_Values, 2, "MÃ SẢN PHẨM", styleHeader);
        createCell_(row_Values, 3, "TÊN SẢN PHẨM", styleHeader);
        createCell_(row_Values, 4, "GIÁ", styleHeader);
        createCell_(row_Values, 5, "SỐ LƯỢNG", styleHeader);
        createCell_(row_Values, 6, "THÀNH TIỀN", styleHeader);

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
    private void createCell_(Row row, int columnCount, Object value, CellStyle style) {
        sheet2.autoSizeColumn(columnCount);
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
        int rowCount_ = 9;

        //////////////////////////////////////////////////////////////////
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
        //////////////////////////////////////////////////////////////////

        for (SoConfirmDTO soConfirm : soConfirms) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, soConfirm.getId(), styleValues);
            createCell(row, columnCount++, soConfirm.getSoNo(), styleValues);
            createCell(row, columnCount++, soConfirm.getProductCode(), styleValues);
            createCell(row, columnCount++, soConfirm.getProductName(), styleValues);
            createCell(row, columnCount++, soConfirm.getProductPrice(), styleValues);
            createCell(row, columnCount++, soConfirm.getQuantity(), styleValues);
            createCell(row, columnCount++, soConfirm.getPriceTotal(), styleValues);
        }
        for (SoConfirmDTO soConfirm2 : soConfirms2) {
            Row row =sheet2.createRow(rowCount_++);
            int columnCount = 0;

            createCell_(row, columnCount++, soConfirm2.getId(), styleValues);
            createCell_(row, columnCount++, soConfirm2.getSoNo(), styleValues);
            createCell_(row, columnCount++, soConfirm2.getProductCode(), styleValues);
            createCell_(row, columnCount++, soConfirm2.getProductName(), styleValues);
            createCell_(row, columnCount++, soConfirm2.getProductPrice(), styleValues);
            createCell_(row, columnCount++, soConfirm2.getQuantity(), styleValues);
            createCell_(row, columnCount++, soConfirm2.getPriceTotal(), styleValues);
        }
    }
    public  ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());

    }
}
*/
