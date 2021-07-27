package vn.viettel.sale.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.sale.service.dto.HDDTExcelDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class HDDTExcel {
    private static final String FONT_NAME= "Times New Roman";

    private SXSSFWorkbook workbook =new SXSSFWorkbook();
    private SXSSFSheet sheet1;

    private List<HDDTExcelDTO> hddtExcelDTOS;

    private XSSFCellStyle styleTableHeader;
    private CellStyle styleTableValue;
    private XSSFCellStyle styleCellTotalTable;

    private Map<String, CellStyle> stylemap = ExcelPoiUtils.createStyles(workbook);
    private CellStyle formatCurrency = stylemap.get(ExcelPoiUtils.DATA_CURRENCY);



    public  HDDTExcel(List<HDDTExcelDTO> hddtExcelDTOS) {
        this.hddtExcelDTOS = hddtExcelDTOS;
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
        font2.setFontHeight(11);
        font2.setFontName(FONT_NAME);
        style2.setFont(font2);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        List<SXSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("HD");
        sheets.add(sheet1);

        for(SXSSFSheet sheet: sheets) {
            int col = 1, row = 0, colm = 17, rowm = 0;
            ExcelPoiUtils.addCellsAndMerged(sheet, col, row, colm, rowm, "VIETTEL", style2);
            col = 18;
            colm = 22;
            ExcelPoiUtils.addCellsAndMerged(sheet, col, row, colm, rowm, "VNPT", style2);
        }
    }

    private void createTableSheet1() {
        int rowTable = 1;

        Row rowHeader = sheet1.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowHeader, 0, "STT", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 2, "MÃ KH", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 3, "NGƯỜI MUA HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 4, "TÊN ĐƠN VỊ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 5, "ĐỊA CHỈ ĐƠN VỊ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 6, "MÃ SỐ THUẾ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 7, "SỐ ĐIỆN THOẠI", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 8, "HÌNH THỨC THANH TOÁN", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 9, "SỐ ĐƠN ĐẶT HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 10, "MÃ HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 11, "TÊN HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 12, "DVT", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 13, "SỐ LƯỢNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 14, "ĐƠN GIÁ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 15, "THÀNH TIỀN", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 16, "THUẾ GTGT (%)", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 17, "GHI CHÚ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 18, "MẪU HÓA ĐƠN", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 19, "SỐ HÓA ĐƠN", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 20, "TẢI FILE PDF", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 21, "KEY", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 22, "SERIAL", styleTableHeader);
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 22);

        if(!hddtExcelDTOS.isEmpty()) {
            for (int i = 0; i < hddtExcelDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                HDDTExcelDTO record = hddtExcelDTOS.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getShopCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getCustomerCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getBuyerName(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOfficeWorking(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOfficeAddress(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getTaxCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getMobiPhone(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPaymentType() == 1 ? "Chuyển khoản" : "Tiền mặt", styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderNumbers(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getUom1(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getQuantity(), formatCurrency);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPriceNotVat(), formatCurrency);
                ExcelPoiUtils.createCell(rowValue, column++, record.getTotalAmount(), formatCurrency);
                ExcelPoiUtils.createCell(rowValue, column++, record.getGTGT(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getNote(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, null, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, null, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, null, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, null, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, null, styleTableValue);
            }
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 22);
    }

    private XSSFCellStyle getTableHeaderStyle() {
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

    private XSSFCellStyle getTableTotalHeaderStyle() {
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

    private CellStyle getTableValueStyle() {
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
