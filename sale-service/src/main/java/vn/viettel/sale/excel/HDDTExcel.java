package vn.viettel.sale.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.service.dto.HDDTExcelDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HDDTExcel {
    private static final String FONT_NAME= "Times New Roman";

    private XSSFWorkbook workbook;
    private XSSFSheet sheet1;

    private List<HDDTExcelDTO> hddtExcelDTOS;

    private XSSFCellStyle styleTableHeader;
    private CellStyle styleTableValue;
    private XSSFCellStyle styleCellTotalTable;

    public  HDDTExcel(List<HDDTExcelDTO> hddtExcelDTOS) {
        this.hddtExcelDTOS = hddtExcelDTOS;

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
        sheet1 = workbook.createSheet("HD");
        sheets.add(sheet1);
    }

    private void createTableSheet1() {
        int rowTable = 0;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", styleTableHeader);
        createCell(sheet1, rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 2, "MÃ KH", styleTableHeader);
        createCell(sheet1, rowHeader, 3, "NGƯỜI MUA HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 4, "TÊN ĐƠN VỊ", styleTableHeader);
        createCell(sheet1, rowHeader, 5, "ĐỊA CHỈ ĐƠN VỊ", styleTableHeader);
        createCell(sheet1, rowHeader, 6, "MÃ SỐ THUẾ", styleTableHeader);
        createCell(sheet1, rowHeader, 7, "SỐ ĐIỆN THOẠI", styleTableHeader);
        createCell(sheet1, rowHeader, 8, "HÌNH THỨC THANH TOÁN", styleTableHeader);
        createCell(sheet1, rowHeader, 9, "SỐ ĐƠN ĐẶT HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 10, "MÃ HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 11, "TÊN HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 12, "DVT", styleTableHeader);
        createCell(sheet1, rowHeader, 13, "SỐ LƯỢNG", styleTableHeader);
        createCell(sheet1, rowHeader, 14, "ĐƠN GIÁ", styleTableHeader);
        createCell(sheet1, rowHeader, 15, "THÀNH TIỀN", styleTableHeader);
        createCell(sheet1, rowHeader, 16, "THUẾ GTGT (%)", styleTableHeader);
        createCell(sheet1, rowHeader, 17, "GHI CHÚ", styleTableHeader);

        if(!hddtExcelDTOS.isEmpty()) {
            for (int i = 0; i < hddtExcelDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                HDDTExcelDTO record = hddtExcelDTOS.get(i);

                createCell(sheet1, rowValue, column++, i + 1, styleTableValue);
                createCell(sheet1, rowValue, column++, record.getShopCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getCustomerCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getBuyerName(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getOfficeWorking(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getOfficeAddress(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getTaxCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getMobiPhone(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getPaymentType() == 1 ? "Chuyển khoản" : "Tiền mặt", styleTableValue);
                createCell(sheet1, rowValue, column++, record.getOrderNumbers(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getProductCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getProductName(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getUom1(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getQuantity(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getPriceNotVat(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getTotalAmount(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getGTGT(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getNote(), styleTableValue);
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

    public String parseToStringDate(Date date) {
        Calendar c = Calendar.getInstance();
        if (date == null) return null;
        c.setTime(date);
        String day = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH) + "";
        String month = c.get(Calendar.MONTH) + 1 < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1) + "";
        String year = c.get(Calendar.YEAR) + "";
        return day + "/" + month + "/" + year;
    }

    private String parseToStringDateTime(Timestamp date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        return dateFormat.format(date);
    }


    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
