//package vn.viettel.report.service.excel;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.streaming.SXSSFSheet;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//import org.apache.poi.xssf.usermodel.*;
//import vn.viettel.core.dto.ShopDTO;
//import vn.viettel.core.util.DateUtils;
//import vn.viettel.core.utils.ExcelPoiUtils;
//import vn.viettel.report.messaging.TotalReport;
//import vn.viettel.report.service.dto.ShopExportDTO;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ExportGoodsExcel {
//    private static final String FONT_NAME= "Times New Roman";
//
//    private SXSSFWorkbook workbook;
//    private SXSSFSheet sheet1;
//
//    private ShopDTO shopDTO;
//    private List<ShopExportDTO> exportGoodsDTOS;
//    private TotalReport totalReport;
//    private LocalDate fromDate;
//    private LocalDate toDate;
//
//    private XSSFCellStyle styleTableHeader;
//    private CellStyle styleTableValue;
//    private XSSFCellStyle styleCellTotalTable;
//
//    public  ExportGoodsExcel(
//            ShopDTO shopDTO, List<ShopExportDTO> exportGoodsDTOS, TotalReport totalReport) {
//        this.shopDTO = shopDTO;
//        this.exportGoodsDTOS = exportGoodsDTOS;
//        this.totalReport = totalReport;
//
//        workbook = new SXSSFWorkbook();
//        this.styleTableHeader = this.getTableHeaderStyle();
//        this.styleCellTotalTable = this.getTableTotalHeaderStyle();
//        this.styleTableValue = this.getTableValueStyle();
//    }
//
//    private void writeHeaderLine()  {
//
//        CellStyle style = workbook.createCellStyle();
//        XSSFFont font = (XSSFFont) workbook.createFont();
//        font.setBold(true);
//        font.setItalic(true);
//        font.setFontHeight(15);
//        font.setFontName(FONT_NAME);
//        style.setFont(font);
//        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//
//        CellStyle style1 = workbook.createCellStyle();
//        XSSFFont font1 = (XSSFFont) workbook.createFont();
//        font1.setBold(false);
//        font1.setItalic(true);
//        font1.setFontHeight(11);
//        font1.setFontName(FONT_NAME);
//        style1.setFont(font1);
//        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//
//        CellStyle style2 = workbook.createCellStyle();
//        XSSFFont font2 = (XSSFFont) workbook.createFont();
//        font2.setBold(true);
//        font2.setItalic(false);
//        font2.setFontHeight(15);
//        font2.setFontName(FONT_NAME);
//        style2.setFont(font2);
//        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//
//        List<SXSSFSheet> sheets = new ArrayList<>();
//        sheet1 = workbook.createSheet("XH_ChiTiet");
//        sheets.add(sheet1);
//
//        for(SXSSFSheet sheet: sheets) {
//            Row row = sheet.createRow(0);
//            Row row1 = sheet.createRow(1);
//            Row row2 = sheet.createRow(2);
//            Row row5 = sheet.createRow(5);
//            Row row7 = sheet.createRow(7);
//
//            row.setRowStyle(style);
//            row1.setRowStyle(style1);
//            row2.setRowStyle(style1);
//            row5.setRowStyle(style2);
//
//            sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I1"));
//            sheet.addMergedRegion(CellRangeAddress.valueOf("J1:S1"));
//            ExcelPoiUtils.createCell(row, 0, shopDTO.getShopName(), style);
//            ExcelPoiUtils.createCell(row, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);
//
//            sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
//            sheet.addMergedRegion(CellRangeAddress.valueOf("J2:S2"));
//            ExcelPoiUtils.createCell(row1, 0, shopDTO.getAddress(), style1);
//            ExcelPoiUtils.createCell(row1, 9, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style1);
//
//            sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
//            sheet.addMergedRegion(CellRangeAddress.valueOf("J3:S3"));
//            ExcelPoiUtils.createCell(row2, 0,"Tel: " + shopDTO.getMobiPhone() + " Fax: " + shopDTO.getFax(), style1);
//            ExcelPoiUtils.createCell(row2, 9, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style1);
//
//            sheet.addMergedRegion(CellRangeAddress.valueOf("A6:W6"));
//            ExcelPoiUtils.createCell(row5, 0, "BÁO CÁO XUẤT HÀNG CHI TIẾT", style2);
//
//            sheet.addMergedRegion(CellRangeAddress.valueOf("A8:W8"));
//            ExcelPoiUtils.createCell(row7, 0, "TỪ NGÀY: " +
//                    DateUtils.formatDate2StringDate(fromDate) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(toDate), style1);
//        }
//    }
//
//    private void createTableSheet1() {
//        int rowTable = 9;
//
//        Row rowHeader = sheet1.createRow(rowTable++);
//        ExcelPoiUtils.createCell(rowHeader, 0, "STT", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 1, "NGÀY XUẤT", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 2, "LOẠI XUẤT", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 3, "MÃ XUẤT HÀNG", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 4, "SỐ HÓA ĐƠN", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 5, "SỐ PO", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 6, "SỐ NỘI BỘ", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 7, "NGÀNH HÀNG", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 8, "MÃ SẢN PHẨM", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 9, "TÊN SẢN PHẨM", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 10, "SỐ LƯỢNG", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 11, "SL PACKET", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 12, "SL LẺ", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 13, "GIÁ TRƯỚC THUẾ", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 14, "THÀNH TIỀN", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 15, "GIÁ SAU THUẾ", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 16, "THÀNH TIỀN", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 17, "DVT PACKET", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 18, "DVT LẺ", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 19, "CỬA HÀNG", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 20, "CHUỖI CỬA HÀNG", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 21, "NHÓM SẢN PHẨM", styleTableHeader);
//        ExcelPoiUtils.createCell(rowHeader, 22, "GHI CHÚ", styleTableHeader);
//
//        if(!exportGoodsDTOS.isEmpty()) {
//            Row rowTotalHeader = sheet1.createRow(rowTable++);
//            ExcelPoiUtils.createCell(rowTotalHeader, 4, "Tổng:", styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 5, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 6, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 7, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 8, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 9, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 10, totalReport.getTotalQuantity(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 11, totalReport.getTotalPacketQuantity(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 12, totalReport.getTotalUnitQuantity(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 13, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 14, totalReport.getTotalAmountNotVat(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 15, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalHeader, 16, totalReport.getTotalAmount(), styleCellTotalTable);
//
//            for (int i = 0; i < exportGoodsDTOS.size(); i++) {
//                int column = 0;
//                Row rowValue = sheet1.createRow(rowTable++);
//                ShopExportDTO record = exportGoodsDTOS.get(i);
//
//                ExcelPoiUtils.createCell(rowValue, column++, i + 1, styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, this.parseToStringDateTime(record.getExportDate()), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getExportType(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getTranCode(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderNumber(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getPoNumber(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getInternalNumber(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCategory(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCode(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getQuantity(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getPacketQuantity(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getUnitQuantity(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getPriceNotVat(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getAmountNotVat(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getPrice(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getTotalAmount(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getPacketUnit(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getUnit(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getShopName(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getShopType(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getProductGroupCategory(), styleTableValue);
//                ExcelPoiUtils.createCell(rowValue, column++, record.getNoted(), styleTableValue);
//
//            }
//
//            Row rowTotalFooter = sheet1.createRow(rowTable++);
//            ExcelPoiUtils.createCell(rowTotalFooter, 4, "Tổng:", styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 5, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 6, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 7, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 8, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 9, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 10, totalReport.getTotalQuantity(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 11, totalReport.getTotalPacketQuantity(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 12, totalReport.getTotalUnitQuantity(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 13, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 14, totalReport.getTotalAmountNotVat(), styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 15, null, styleCellTotalTable);
//            ExcelPoiUtils.createCell(rowTotalFooter, 16, totalReport.getTotalAmount(), styleCellTotalTable);
//        }
//        ExcelPoiUtils.autoSizeAllColumns(sheet1, 22);
//    }
//
//    public XSSFCellStyle getTableHeaderStyle() {
//        CellStyle styleHeader1 = workbook.createCellStyle();
//        XSSFFont fontHeader = (XSSFFont) workbook.createFont();
//        fontHeader.setBold(true);
//        fontHeader.setItalic(false);
//        fontHeader.setFontHeight(10);
//        fontHeader.setFontName(FONT_NAME);
//        styleHeader1.setFont(fontHeader);
//        byte[] rgb = new byte[]{(byte)192, (byte)192, (byte)192};
//        XSSFCellStyle styleHeader = (XSSFCellStyle)styleHeader1;
//        XSSFColor colorHeader = new XSSFColor(rgb,null);
//        styleHeader.setFillForegroundColor(colorHeader);
//        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        styleHeader.setAlignment(HorizontalAlignment.CENTER);
//        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
//        styleHeader.setBorderTop(BorderStyle.THIN);
//        styleHeader.setBorderBottom(BorderStyle.THIN);
//        styleHeader.setBorderLeft(BorderStyle.THIN);
//        styleHeader.setBorderRight(BorderStyle.THIN);
//
//        return styleHeader;
//    }
//
//    public XSSFCellStyle getTableTotalHeaderStyle() {
//        CellStyle totalRowStyle = workbook.createCellStyle();
//        XSSFFont fontTotal = (XSSFFont) workbook.createFont();
//        fontTotal.setFontHeight(10);
//        fontTotal.setFontName(FONT_NAME);
//        fontTotal.setBold(true);
//        totalRowStyle.setFont(fontTotal);
//
//        byte[] rgb = new byte[]{(byte)255, (byte)204, (byte)153};
//        XSSFCellStyle totalRowStyleRGB = (XSSFCellStyle)totalRowStyle;
//        XSSFColor customColor = new XSSFColor(rgb,null);
//        totalRowStyleRGB.setFillForegroundColor(customColor);
//        totalRowStyleRGB.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        totalRowStyleRGB.setBorderBottom(BorderStyle.THIN);
//        totalRowStyleRGB.setBorderTop(BorderStyle.THIN);
//        totalRowStyleRGB.setBorderLeft(BorderStyle.THIN);
//        totalRowStyleRGB.setBorderRight(BorderStyle.THIN);
//
//        return totalRowStyleRGB;
//    }
//
//    public CellStyle getTableValueStyle() {
//        CellStyle styleValues = workbook.createCellStyle();
//        XSSFFont fontValues = (XSSFFont) workbook.createFont();
//        fontValues.setBold(false);
//        fontValues.setItalic(false);
//        fontValues.setFontHeight(9);
//        fontValues.setFontName("Times New Roman");
//        styleValues.setFont(fontValues);
//        styleValues.setBorderTop(BorderStyle.THIN);
//        styleValues.setBorderBottom(BorderStyle.THIN);
//        styleValues.setBorderLeft(BorderStyle.THIN);
//        styleValues.setBorderRight(BorderStyle.THIN);
//
//        return styleValues;
//    }
//
//    private String parseToStringDateTime(Timestamp date) {
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
//        return dateFormat.format(date);
//    }
//
//    public void setFromDate(LocalDate fromDate) {
//        this.fromDate = fromDate;
//    }
//
//    public void setToDate(LocalDate toDate) {
//        this.toDate = toDate;
//    }
//
//
//    public ByteArrayInputStream export() throws IOException {
//        this.writeHeaderLine();
//        this.createTableSheet1();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        workbook.write(out);
//        return new ByteArrayInputStream(out.toByteArray());
//    }
//
//}
