package vn.viettel.sale.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.sale.service.dto.CTDTO;
import vn.viettel.sale.service.dto.HDDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class HVKHExcel {
    private String FONT_NAME= "Times New Roman";
    private SXSSFWorkbook workbook = new SXSSFWorkbook();
    private SXSSFSheet sheet1;
    private SXSSFSheet sheet2;

    private List<HDDTO> hddtos;
    private List<CTDTO> ctdtos;

    private XSSFCellStyle styleTableHeader;
    private CellStyle styleTableValue;
    private XSSFCellStyle styleCellTotalTable;

    private Map<String, CellStyle> stylemap = ExcelPoiUtils.createStyles(workbook);
    private CellStyle formatCurrency = stylemap.get(ExcelPoiUtils.DATA_CURRENCY);

    Map<String, CellStyle> style;
    public  HVKHExcel(List<HDDTO> hddtos, List<CTDTO> ctdtos) {
        this.hddtos = hddtos;
        this.ctdtos = ctdtos;

        this.styleTableHeader = this.getTableHeaderStyle();
        this.styleCellTotalTable = this.getTableTotalHeaderStyle();
        this.styleTableValue = this.getTableValueStyle();
    }

    private void writeHeaderLine()  {

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(true);
        font.setItalic(true);
        font.setFontHeight(15);
        font.setFontName(FONT_NAME);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = (XSSFFont) workbook.createFont();
        font1.setBold(false);
        font1.setItalic(true);
        font1.setFontHeight(11);
        font1.setFontName(FONT_NAME);
        style1.setFont(font1);
        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = (XSSFFont) workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName(FONT_NAME);
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        List<SXSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("DH");
        sheet2 = workbook.createSheet("CT");
        sheets.add(sheet1);
        sheets.add(sheet2);
    }

    private void createTableSheet1() {
        int rowTable = 0;
        Row rowHeader = sheet1.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowHeader, 0, "STT", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 2, "SỐ PO ĐƠN HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 3, "TÊN KHÁCH HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 4, "NGÀY BÁO CÁO THUẾ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 5, "SỐ HÓA ĐƠN", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 6, "MÃ SỐ THUẾ DOANH NGHIỆP", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 7, "TỔNG SỐ TIỀN (VNĐ)", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 8, "KHO", styleTableHeader);
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 8);
        if(!hddtos.isEmpty())
        {
            for (int i = 0; i < hddtos.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                HDDTO record = hddtos.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getShopCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getInvoiceNumber(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getFullName(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDate(record.getPrintDate()), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, null, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getTaxCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getAmountNotVat(), formatCurrency);
                ExcelPoiUtils.createCell(rowValue, column++, record.getWareHouse(), styleTableValue);
            }
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 8);
    }

    private void createTableSheet2() {
        int rowTable = 0;
        Row rowHeader = sheet2.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowHeader, 0, "STT", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 2, "TÊN SHIP TO", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 3, "SỐ HÓA ĐƠN LẺ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 4, "MÃ SẢN PHẨM", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 5, "ĐƠN VỊ TÍNH", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 6, "SỐ LƯỢNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 7, "KHO", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 8, "LOẠI ĐƠN HÀNG", styleTableHeader);
        ExcelPoiUtils.autoSizeAllColumns(sheet2, 22);
        if(!ctdtos.isEmpty())
        {
            for (int i = 0; i < ctdtos.size(); i++) {
                int column = 0;
                Row rowValue = sheet2.createRow(rowTable++);
                CTDTO record = ctdtos.get(i);
                ExcelPoiUtils.createCell(rowValue, column++, i + 1, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getShopCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getShipToName(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getInvoiceNumber(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getUom1(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getQuantity(), formatCurrency);
                ExcelPoiUtils.createCell(rowValue, column++, record.getWareHouse(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getRedInvoiceType(), styleTableValue);
            }
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 8);
    }

    public XSSFCellStyle getTableHeaderStyle() {
        CellStyle styleHeader1 = workbook.createCellStyle();
        XSSFFont fontHeader = (XSSFFont) workbook.createFont();
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
        XSSFFont fontTotal = (XSSFFont) workbook.createFont();
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
        XSSFFont fontValues = (XSSFFont) workbook.createFont();
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

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        this.createTableSheet2();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        IOUtils.closeQuietly(out);
        return response;
    }
}
