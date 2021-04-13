package vn.viettel.report.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExportExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExportExcel() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine()  {
        sheet = workbook.createSheet("Sản phẩm");
        Row row = sheet.createRow(0);
        ////////////////////////////////////////////////////////////////////
        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontheader = workbook.createFont();
        fontheader.setBold(false);
        fontheader.setItalic(false);
        fontheader.setFontHeight(10);
        fontheader.setFontName("Times New Roman");
        styleHeader.setFont(fontheader);
        styleHeader.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        int rowf = 0;
        createCell(row, rowf, "Mã sản phẩm", styleHeader);
        createCell(row, rowf +1, "Tên sản phẩm", styleHeader);
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
        int rowCount =1;

        //////////////////////////////////////////////////////////////////
        CellStyle styleValues = workbook.createCellStyle();
        XSSFFont fontValues = workbook.createFont();
        fontValues.setBold(false);
        fontValues.setItalic(false);
        fontValues.setFontHeight(9);
        styleValues.setFont(fontValues);
        styleValues.setBorderTop(BorderStyle.THIN);
        styleValues.setBorderBottom(BorderStyle.THIN);
        styleValues.setBorderLeft(BorderStyle.THIN);
        styleValues.setBorderRight(BorderStyle.THIN);
        //////////////////////////////////////////////////////////////////
        Row row = sheet.createRow(rowCount++);
        int columnCount = 0;

        createCell(row, columnCount++,"", styleValues);
        createCell(row, columnCount++,"", styleValues);

    }
    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
