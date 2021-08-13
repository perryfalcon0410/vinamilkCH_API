package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SaleDeliveryTypeExcel {
    private ShopDTO shopDTO;
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private List<SaleByDeliveryTypeDTO> saleDeli;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Map<String, CellStyle> style;

    public SaleDeliveryTypeExcel(ShopDTO shopDTO, List<SaleByDeliveryTypeDTO> saleDeli) {
        workbook = new SXSSFWorkbook();
        {
            this.shopDTO = shopDTO;
            this.saleDeli = saleDeli;
            this.style = ExcelPoiUtils.createStyles(workbook);
        }
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Doanh số");
        ////////// HEADER /////////////////////////////
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J1:Q1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J2:Q2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J3:Q3"));
        Row customerRow = sheet.createRow(0); // name
        Row customerAddressRow = sheet.createRow(1); // address
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setItalic(true);
        headerFont.setFontHeight(15);
        headerFont.setFontName("Times New Roman");
        headerStyle.setFont(headerFont);
        CellStyle addressStyle = workbook.createCellStyle();
        XSSFFont addressFont = (XSSFFont) workbook.createFont();
        addressFont.setItalic(true);
        addressFont.setFontHeight(11);
        addressFont.setFontName("Times New Roman");
        addressStyle.setFont(addressFont);
        Row customerPhoneRow = sheet.createRow(2);// phone

        ExcelPoiUtils.createCell(customerRow, 0, shopDTO.getShopName(), headerStyle);
        ExcelPoiUtils.createCell(customerRow, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", headerStyle);
        ExcelPoiUtils.createCell(customerAddressRow, 0, shopDTO.getAddress(), addressStyle);
        ExcelPoiUtils.createCell(customerAddressRow, 9, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", addressStyle);
        ExcelPoiUtils.createCell(customerPhoneRow, 0, "Tel: " + shopDTO.getMobiPhone(), addressStyle);
        ExcelPoiUtils.createCell(customerPhoneRow, 9, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", addressStyle);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:M6"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A8:M8"));
        Row header = sheet.createRow(5);
        Row dateRow = sheet.createRow(7);
        Row row = sheet.createRow(8);
        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont fontTitle = (XSSFFont) workbook.createFont();
        fontTitle.setFontHeight(15);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);
        titleStyle.setFont(fontTitle);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        XSSFFont fontDate = (XSSFFont) workbook.createFont();
        fontDate.setFontHeight(12);
        fontDate.setFontName("Times New Roman");
        fontDate.setItalic(true);
        dateStyle.setFont(fontDate);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle colNameStyle = workbook.createCellStyle();
        XSSFFont colNameFont = (XSSFFont) workbook.createFont();
        colNameFont.setFontHeight(10);
        colNameFont.setFontName("Times New Roman");
        colNameFont.setBold(true);
        colNameStyle.setFont(colNameFont);
        colNameStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        colNameStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        colNameStyle.setBorderBottom(BorderStyle.THIN);
        colNameStyle.setBorderTop(BorderStyle.THIN);
        colNameStyle.setBorderLeft(BorderStyle.THIN);
        colNameStyle.setBorderRight(BorderStyle.THIN);
        colNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        ExcelPoiUtils.createCell(header, 0, "BÁO CÁO DOANH SỐ THEO LOẠI GIAO HÀNG", titleStyle);
        ExcelPoiUtils.createCell(dateRow, 0, "TỪ NGÀY: " +
                DateUtils.formatDate2StringDate(fromDate) + "   ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(toDate), dateStyle);
        ExcelPoiUtils.createCell(row, 0, "STT", colNameStyle);
        ExcelPoiUtils.createCell(row, 1, "MÃ CỬA HÀNG", colNameStyle);
        ExcelPoiUtils.createCell(row, 2, "TÊN CỬA HÀNG", colNameStyle);
        ExcelPoiUtils.createCell(row, 3, "MÃ KHÁCH HÀNG", colNameStyle);
        ExcelPoiUtils.createCell(row, 4, "TÊN KHÁCH HÀNG", colNameStyle);
        ExcelPoiUtils.createCell(row, 5, "ĐỊA CHỈ", colNameStyle);
        ExcelPoiUtils.createCell(row, 6, "SỐ HÓA ĐƠN", colNameStyle);
        ExcelPoiUtils.createCell(row, 7, "DOANH SỐ", colNameStyle);
        ExcelPoiUtils.createCell(row, 8, "THANH TOÁN", colNameStyle);
        ExcelPoiUtils.createCell(row, 9, "NGÀY BÁN", colNameStyle);
        ExcelPoiUtils.createCell(row, 10, "LOẠI GIAO HÀNG", colNameStyle);
        ExcelPoiUtils.createCell(row, 11, "SỐ ĐƠN ONLINE", colNameStyle);
        ExcelPoiUtils.createCell(row, 12, "LOẠI", colNameStyle);
        ExcelPoiUtils.autoSizeAllColumns(sheet, 12);

        CellStyle dataStyle = workbook.createCellStyle();
        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontHeight(9);
        dataFont.setFontName("Times New Roman");
        dataStyle.setFont(dataFont);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dataStyle2 = dataStyle;
        DataFormat dataFormat = workbook.createDataFormat();
        dataStyle2.setDataFormat(dataFormat.getFormat("#,###"));

        if (!saleDeli.isEmpty()) {
            int start = 9;
            for (int i = 0; i < saleDeli.size() - 2; i++) {
                Row rowContent = sheet.createRow(start);
                SaleByDeliveryTypeDTO record = saleDeli.get(i);
                ExcelPoiUtils.createCell(rowContent, 0, i + 1, dataStyle);
                ExcelPoiUtils.createCell(rowContent, 1, record.getShopCode(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 2, record.getShopName(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 3, record.getCustomerCode(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 4, record.getCustomerName(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 5, record.getCustomerAddress(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 6, record.getOrderNumber(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 7, record.getAmount(), dataStyle2);
                ExcelPoiUtils.createCell(rowContent, 8, record.getTotal(), dataStyle2);
                ExcelPoiUtils.createCell(rowContent, 9, DateUtils.formatDate2StringDate(record.getOrderDate()), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 10, record.getDeliveryType(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 11, record.getOnlineNumber(), dataStyle);
                ExcelPoiUtils.createCell(rowContent, 12, record.getType(), dataStyle);
                start++;
            }
        }

        ExcelPoiUtils.autoSizeAllColumns(sheet, 12);
    }

        public void setFromDate (LocalDateTime fromDate){
            this.fromDate = fromDate;
        }

        public void setToDate (LocalDateTime toDate){
            this.toDate = toDate;
        }

        public ByteArrayInputStream export () throws IOException {
            this.writeHeaderLine();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
            workbook.close();
            out.close();
            return response;
        }
}
