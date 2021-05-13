package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ReturnGoodsDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EntryMenuDetailsExcel {
    private static final String FONT_NAME= "Times New Roman";

    private XSSFWorkbook workbook;
    private XSSFSheet sheet1;
    private XSSFSheet sheet2;
    private XSSFSheet sheet3;

    private ShopDTO shopDTO;
    private List<EntryMenuDetailsDTO> entryMenuDetailsDTOS;
    private EntryMenuDetailsDTO entryMenuDetailsDTO;
    private Date fromDate;
    private Date toDate;

    private XSSFCellStyle styleTableHeader;
    private CellStyle styleTableValue;
    private XSSFCellStyle styleCellTotalTable;

    public EntryMenuDetailsExcel(
            ShopDTO shopDTO, List<EntryMenuDetailsDTO> entryMenuDetailsDTOS, EntryMenuDetailsDTO total) {
        this.shopDTO = shopDTO;
        this.entryMenuDetailsDTOS = entryMenuDetailsDTOS;
        this.entryMenuDetailsDTO = total;

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
        sheet1 = workbook.createSheet("sheet1");
        sheets.add(sheet1);


        for(XSSFSheet sheet: sheets) {
            Row row = sheet.createRow(0);
            Row row1 = sheet.createRow(1);
            Row row2 = sheet.createRow(2);
            Row row5 = sheet.createRow(5);
            Row row7 = sheet.createRow(7);

            row.setRowStyle(style);
            row1.setRowStyle(style1);
            row2.setRowStyle(style1);
            row5.setRowStyle(style2);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I1"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J1:Q1"));
            createCell(sheet, row, 0, shopDTO.getShopName(), style);
            createCell(sheet, row, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J2:Q2"));
            createCell(sheet, row1, 0, shopDTO.getAddress(), style1);
            createCell(sheet, row1, 9, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style1);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J3:Q3"));
            createCell(sheet, row2, 0,"Tel: " + shopDTO.getMobiPhone() + " Fax: " + shopDTO.getFax(), style1);
            createCell(sheet, row2, 9, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style1);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:N6"));
            createCell(sheet, row5, 0, "BÁO CÁO BẢNG KIỂM KÊ CHI TIẾT HÓA ĐƠN NHẬP HÀNG", style2);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A8:N8"));
            createCell(sheet, row7, 0, "TỪ NGÀY: " +
                    this.parseToStringDate(fromDate) + " ĐẾN NGÀY: " + this.parseToStringDate(toDate), style1);
        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", styleTableHeader);
        createCell(sheet1, rowHeader, 1, "SOPO", styleTableHeader);
        createCell(sheet1, rowHeader, 2, "SONOIBO", styleTableHeader);
        createCell(sheet1, rowHeader, 3, "SOHD", styleTableHeader);
        createCell(sheet1, rowHeader, 4, "NGAYHD", styleTableHeader);
        createCell(sheet1, rowHeader, 5, "NGAYTT", styleTableHeader);
        createCell(sheet1, rowHeader, 6, "SOTIEN", styleTableHeader);
        createCell(sheet1, rowHeader, 7, "HDKM", styleTableHeader);



        if(!entryMenuDetailsDTOS.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalHeader, 4, "Tổng:" ,styleCellTotalTable);
            createCell(sheet1, rowTotalHeader, 5, null , styleCellTotalTable);
            createCell(sheet1, rowTotalHeader, 6, this.entryMenuDetailsDTO.getTotalAmount(), styleCellTotalTable);
            createCell(sheet1, rowTotalHeader, 7, null, styleCellTotalTable);


            for (int i = 0; i < entryMenuDetailsDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                EntryMenuDetailsDTO record = entryMenuDetailsDTOS.get(i);

                createCell(sheet1, rowValue, column++, i + 1, styleTableValue);
                createCell(sheet1, rowValue, column++, record.getPoNumber(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getInternalNumber(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getRedInvoiceNo(), styleTableValue);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(record.getBillDate());
                createCell(sheet1, rowValue, column++, strDate, styleTableValue);
                String dateOfPayment = dateFormat.format(record.getDateOfPayment());
                createCell(sheet1, rowValue, column++, dateOfPayment, styleTableValue);
                createCell(sheet1, rowValue, column++, record.getAmount(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getPromotionalOrders(), styleTableValue);
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalFooter, 4, "Tổng:", styleCellTotalTable);
            createCell(sheet1, rowTotalFooter, 5, null , styleCellTotalTable);
            createCell(sheet1, rowTotalFooter, 6, this.entryMenuDetailsDTO.getTotalAmount(), styleCellTotalTable);
            createCell(sheet1, rowTotalFooter, 7, null, styleCellTotalTable);

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

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }


    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
