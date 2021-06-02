package vn.viettel.report.service.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.dto.ExportGoodsDTO;
import vn.viettel.report.service.dto.ReportVoucherDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportVoucherExcel {
    private static final String FONT_NAME= "Times New Roman";

    private XSSFWorkbook workbook;
    private XSSFSheet sheet1;

    private ShopDTO shopDTO;
    private List<ReportVoucherDTO> reportVoucherDTOS;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDate fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDate toDate;

    private XSSFCellStyle styleTableHeader;
    private CellStyle styleTableValue;
    private XSSFCellStyle styleCellTotalTable;

    public  ReportVoucherExcel(
            ShopDTO shopDTO, List<ReportVoucherDTO> reportVoucherDTOS) {
        this.shopDTO = shopDTO;
        this.reportVoucherDTOS = reportVoucherDTOS;

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
        sheet1 = workbook.createSheet("Voucher_ChiTiet");
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
            sheet.addMergedRegion(CellRangeAddress.valueOf("J1:S1"));
            createCell(sheet, row, 0, shopDTO.getShopName(), style);
            createCell(sheet, row, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J2:S2"));
            createCell(sheet, row1, 0, shopDTO.getAddress(), style1);
            createCell(sheet, row1, 9, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style1);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J3:S3"));
            createCell(sheet, row2, 0,"Tel: " + shopDTO.getMobiPhone() + " Fax: " + shopDTO.getFax(), style1);
            createCell(sheet, row2, 9, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style1);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:W6"));
            createCell(sheet, row5, 0, "BÁO CÁO DANH SÁCH VOUCHER", style2);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A8:W8"));
            createCell(sheet, row7, 0, "TỪ NGÀY: " +
                    DateUtils.formatDate2StringDate(fromDate) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(toDate), style1);
        }
    }

    private void createTableSheet1() {
        int rowTable = 9;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", styleTableHeader);
        createCell(sheet1, rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 2, "MÃ CHƯƠNG TRÌNH VOUCHER", styleTableHeader);
        createCell(sheet1, rowHeader, 3, "TÊN CHƯƠNG TRÌNH VOUCHER", styleTableHeader);
        createCell(sheet1, rowHeader, 4, "MÃ VOUCHER", styleTableHeader);
        createCell(sheet1, rowHeader, 5, "TÊN VOUCHER", styleTableHeader);
        createCell(sheet1, rowHeader, 6, "SỐ SERIAL", styleTableHeader);
        createCell(sheet1, rowHeader, 7, "MỆNH GIÁ", styleTableHeader);
        createCell(sheet1, rowHeader, 8, "NGƯỜI CHUYỂN CỬA HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 9, "NGÀY CHUYỂN CỬA HÀNG", styleTableHeader);
        createCell(sheet1, rowHeader, 10, "TRẠNG THÁI HOẠT ĐỘNG", styleTableHeader);
        createCell(sheet1, rowHeader, 11, "TRẠNG THÁI KÍCH HOẠT", styleTableHeader);
        createCell(sheet1, rowHeader, 12, "TRẠNG THÁI SỬ DỤNG", styleTableHeader);
        createCell(sheet1, rowHeader, 13, "KHÁCH HÀNG SỬ DỤNG", styleTableHeader);
        createCell(sheet1, rowHeader, 14, "ĐƠN HÀNG SỬ DỤNG", styleTableHeader);
        createCell(sheet1, rowHeader, 15, "CỬA HÀNG SỬ DỤNG", styleTableHeader);
        createCell(sheet1, rowHeader, 16, "NGÀY SỬ DỤNG", styleTableHeader);
        createCell(sheet1, rowHeader, 17, "DOANH SỐ", styleTableHeader);

        if(!reportVoucherDTOS.isEmpty()) {

            for (int i = 0; i < reportVoucherDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                ReportVoucherDTO record = reportVoucherDTOS.get(i);

                createCell(sheet1, rowValue, column++, i + 1, styleTableValue);
                createCell(sheet1, rowValue, column++, record.getShopCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getShopName(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getVoucherProgramName(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getVoucherCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getVoucherName(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getSerial(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getPrice(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getChangeUser(), styleTableValue);
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getChangeDate()), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getStatus(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getActivated(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getIsUsed(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getCustomerCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getOrderNumber(), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getOrderShopCode(), styleTableValue);
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getOrderDate()), styleTableValue);
                createCell(sheet1, rowValue, column++, record.getOrderAmount(), styleTableValue);

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

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDate toDate) {
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
