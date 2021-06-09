package vn.viettel.sale.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.service.dto.CTDTO;
import vn.viettel.sale.service.dto.HDDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class HVKHExcel {
    private static final String FONT_NAME= "Times New Roman";
    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;
    private XSSFSheet sheet2;

    private List<HDDTO> hddtos;
    private List<CTDTO> ctdtos;

    private XSSFCellStyle styleTableHeader;
    private CellStyle styleTableValue;
    private XSSFCellStyle styleCellTotalTable;

    Map<String, CellStyle> style;
    public  HVKHExcel(List<HDDTO> hddtos, List<CTDTO> ctdtos) {
        this.hddtos = hddtos;
        this.ctdtos = ctdtos;

        workbook = new XSSFWorkbook();
        this.styleTableHeader = this.getTableHeaderStyle();
        this.styleCellTotalTable = this.getTableTotalHeaderStyle();
        this.styleTableValue = this.getTableValueStyle();
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
        sheet1 = workbook.createSheet("DH");
        sheet2 = workbook.createSheet("CT");
        sheets.add(sheet1);
        sheets.add(sheet2);
    }

    private void createTableSheet1() {
        int rowTable = 0;
        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", styleTableHeader);
        createCell(sheet1, rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 2, "SỐ PO ĐƠN HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 3, "TÊN KHÁCH HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 4, "NGÀY BÁO CÁO THUẾ", styleTableHeader);
        createCell(sheet1, rowHeader, 5, "SỐ HÓA ĐƠN", styleTableHeader);
        createCell(sheet1, rowHeader, 6, "MÃ SỐ THUẾ DOANH NGHIỆP", styleTableHeader);
        createCell(sheet1, rowHeader, 7, "TỔNG SỐ TIỀN (VNĐ)", styleTableHeader);
        createCell(sheet1, rowHeader, 8, "KHO", styleTableHeader);

        if(!hddtos.isEmpty())
        {
            for (int i = 0; i < hddtos.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                HDDTO record = hddtos.get(i);

                createCell(sheet1, rowValue, column++, i + 1, styleTableValue);
                createCell(sheet1, rowValue, column++, record.getShopCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getInvoiceNumber(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getFullName(), styleTableValue);
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getPrintDate()), styleTableValue);
                createCell(sheet1, rowValue, column++, null, styleTableValue);
                createCell(sheet1, rowValue, column++, record.getTaxCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getTotalMoney(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getWareHouse(), styleTableValue);
            }
        }
    }

    private void createTableSheet2() {
        int rowTable = 0;
        Row rowHeader = sheet2.createRow(rowTable++);
        createCell(sheet2, rowHeader, 0, "STT", styleTableHeader);
        createCell(sheet2, rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        createCell(sheet2, rowHeader, 2, "TÊN SHOP TO", styleTableHeader);
        createCell(sheet2, rowHeader, 3, "SỐ HÓA ĐƠN LẺ", styleTableHeader);
        createCell(sheet2, rowHeader, 4, "MÃ SẢN PHẨM", styleTableHeader);
        createCell(sheet2, rowHeader, 5, "ĐƠN VỊ TÍNH", styleTableHeader);
        createCell(sheet2, rowHeader, 6, "SỐ LƯỢNG", styleTableHeader);
        createCell(sheet2, rowHeader, 7, "KHO", styleTableHeader);
        createCell(sheet2, rowHeader, 8, "LOẠI ĐƠN HÀNG", styleTableHeader);

        if(!ctdtos.isEmpty())
        {
            for (int i = 0; i < ctdtos.size(); i++) {
                int column = 0;
                Row rowValue = sheet2.createRow(rowTable++);
                CTDTO record = ctdtos.get(i);
                createCell(sheet2, rowValue, column++, i + 1, styleTableValue);
                createCell(sheet2, rowValue, column++, record.getShopCode(), styleTableValue);
                createCell(sheet2, rowValue, column++, record.getShipToName(), styleTableValue);
                createCell(sheet2, rowValue, column++, record.getInvoiceNumber(), styleTableValue);
                createCell(sheet2, rowValue, column++, record.getProductCode(), styleTableValue);
                createCell(sheet2, rowValue, column++, record.getUom1(), styleTableValue);
                createCell(sheet2, rowValue, column++, record.getQuantity(), styleTableValue);
                createCell(sheet2, rowValue, column++, record.getWareHouse(), styleTableValue);
                createCell(sheet2, rowValue, column++, record.getRedInvoiceType(), styleTableValue);
            }
        }
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

    public CellStyle getTableValueStyle() {
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

        return styleValues;
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
