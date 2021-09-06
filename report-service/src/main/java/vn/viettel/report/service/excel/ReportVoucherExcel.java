package vn.viettel.report.service.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.service.dto.ReportVoucherDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportVoucherExcel {
    private static final String FONT_NAME= "Times New Roman";

    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet1;

    private ShopDTO shopDTO;
    private ShopDTO parentShop;
    private List<ReportVoucherDTO> reportVoucherDTOS;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;

    private XSSFCellStyle styleTableHeader;
    private CellStyle styleTableValue;
    private XSSFCellStyle styleCellTotalTable;

    public  ReportVoucherExcel(
            ShopDTO shopDTO, ShopDTO parentShop, List<ReportVoucherDTO> reportVoucherDTOS) {
        this.shopDTO = shopDTO;
        this.parentShop = parentShop;
        this.reportVoucherDTOS = reportVoucherDTOS;

        workbook = new SXSSFWorkbook();
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
        sheet1 = workbook.createSheet("Voucher_ChiTiet");
        sheets.add(sheet1);

        for(SXSSFSheet sheet: sheets) {
            Row row = sheet.createRow(0);
            Row row1 = sheet.createRow(1);
            Row row2 = sheet.createRow(2);
            Row row5 = sheet.createRow(5);
            Row row8 = sheet.createRow(8);

            row.setRowStyle(style);
            row1.setRowStyle(style1);
            row2.setRowStyle(style1);
            row5.setRowStyle(style2);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I1"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J1:S1"));
            ExcelPoiUtils.createCell(row, 0, shopDTO.getShopName(), style);
            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J2:S2"));
            ExcelPoiUtils.createCell(row1, 0, shopDTO.getAddress(), style1);
            sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
            sheet.addMergedRegion(CellRangeAddress.valueOf("J3:S3"));
            ExcelPoiUtils.createCell(row2, 0,"Tel: " + (shopDTO.getPhone()!=null? shopDTO.getPhone():"") + " Fax: " + (shopDTO.getFax()!=null?shopDTO.getFax():""), style1);

            if(parentShop != null) {
                ExcelPoiUtils.createCell(row1, 9, parentShop.getShopName(), style1);
                ExcelPoiUtils.createCell(row, 9, parentShop.getAddress(), style);
                ExcelPoiUtils.createCell(row2, 9, "Tel: " + (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""), style1);
            }

            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:W6"));
            ExcelPoiUtils.createCell(row5, 0, "BÁO CÁO DANH SÁCH VOUCHER", style2);

            sheet.addMergedRegion(CellRangeAddress.valueOf("A8:W8"));
            ExcelPoiUtils.createCell(row8, 0, "TỪ NGÀY: " +
                    DateUtils.formatDate2StringDate(fromDate) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(toDate), style1);
        }
    }

    private void createTableSheet1() {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        int rowTable = 9;

        Row rowHeader = sheet1.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowHeader, 0, "STT", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 1, "MÃ CỬA HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 2, "MÃ CHƯƠNG TRÌNH VOUCHER", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 3, "TÊN CHƯƠNG TRÌNH VOUCHER", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 4, "MÃ VOUCHER", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 5, "TÊN VOUCHER", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 6, "SỐ SERIAL", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 7, "MỆNH GIÁ", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 8, "NGƯỜI CHUYỂN CỬA HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 9, "NGÀY CHUYỂN CỬA HÀNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 10, "TRẠNG THÁI HOẠT ĐỘNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 11, "TRẠNG THÁI KÍCH HOẠT", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 12, "TRẠNG THÁI SỬ DỤNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 13, "KHÁCH HÀNG SỬ DỤNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 14, "ĐƠN HÀNG SỬ DỤNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 15, "CỬA HÀNG SỬ DỤNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 16, "NGÀY SỬ DỤNG", styleTableHeader);
        ExcelPoiUtils.createCell(rowHeader, 17, "DOANH SỐ", styleTableHeader);

        if(!reportVoucherDTOS.isEmpty()) {

            for (int i = 0; i < reportVoucherDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                ReportVoucherDTO record = reportVoucherDTOS.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getShopCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getShopName(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getVoucherProgramName(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getVoucherCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getVoucherName(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getSerial(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPrice(), formatCurrency);
                ExcelPoiUtils.createCell(rowValue, column++, record.getChangeUser(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDate(record.getChangeDate()), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getStatus(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getActivated(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getIsUsed(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getCustomerCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderNumber(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderShopCode(), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDate(record.getOrderDate()), styleTableValue);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderAmount(), formatCurrency);
            }
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 17);
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

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        IOUtils.closeQuietly(out);
        return response;
    }

}
