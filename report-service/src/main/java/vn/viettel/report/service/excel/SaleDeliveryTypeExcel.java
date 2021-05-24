package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SaleDeliveryTypeExcel {
    private ShopDTO shopDTO;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<SaleByDeliveryTypeDTO> saleDeli;
    private Date fromDate;
    private Date toDate;
    Map<String, CellStyle> style;

    public SaleDeliveryTypeExcel(ShopDTO shopDTO, List<SaleByDeliveryTypeDTO> saleDeli) {
        workbook = new XSSFWorkbook();
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
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setItalic(true);
        headerFont.setFontHeight(15);
        headerFont.setFontName("Times New Roman");
        headerStyle.setFont(headerFont);
        CellStyle addressStyle = workbook.createCellStyle();
        XSSFFont addressFont = workbook.createFont();
        addressFont.setItalic(true);
        addressFont.setFontHeight(11);
        addressFont.setFontName("Times New Roman");
        addressStyle.setFont(addressFont);
        Row customerPhoneRow = sheet.createRow(2);// phone

        createCell(customerRow, 0, shopDTO.getShopName(), headerStyle);
        createCell(customerRow, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", headerStyle);
        createCell(customerAddressRow, 0, shopDTO.getAddress(), addressStyle);
        createCell(customerAddressRow, 9, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", addressStyle);
        createCell(customerPhoneRow, 0, "Tel: " + shopDTO.getMobiPhone(), addressStyle);
        createCell(customerPhoneRow, 9, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", addressStyle);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:M6"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A8:M8"));
        Row header = sheet.createRow(5);
        Row dateRow = sheet.createRow(7);
        Row row = sheet.createRow(8);
        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont fontTitle = workbook.createFont();
        fontTitle.setFontHeight(15);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);
        titleStyle.setFont(fontTitle);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        XSSFFont fontDate = workbook.createFont();
        fontDate.setFontHeight(12);
        fontDate.setFontName("Times New Roman");
        fontDate.setItalic(true);
        dateStyle.setFont(fontDate);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle colNameStyle = workbook.createCellStyle();
        XSSFFont colNameFont = workbook.createFont();
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

        createCell(header, 0, "BÁO CÁO DOANH SỐ THEO LOẠI GIAO HÀNG", titleStyle);
        createCell(dateRow, 0, "TỪ NGÀY: " +
                this.parseToStringDate(fromDate) + "   ĐẾN NGÀY: " + this.parseToStringDate(toDate), dateStyle);
        createCell(row, 0, "STT", colNameStyle);
        createCell(row, 1, "MÃ CỬA HÀNG", colNameStyle);
        createCell(row, 2, "TÊN CỬA HÀNG", colNameStyle);
        createCell(row, 3, "MÃ KHÁCH HÀNG", colNameStyle);
        createCell(row, 4, "TÊN KHÁCH HÀNG", colNameStyle);
        createCell(row, 5, "ĐỊA CHỈ", colNameStyle);
        createCell(row, 6, "SỐ HÓA ĐƠN", colNameStyle);
        createCell(row, 7, "DOANH SỐ", colNameStyle);
        createCell(row, 8, "THANH TOÁN", colNameStyle);
        createCell(row, 9, "NGÀY BÁN", colNameStyle);
        createCell(row, 10, "LOẠI GIAO HÀNG", colNameStyle);
        createCell(row, 11, "SỐ ĐƠN ONLINE", colNameStyle);
        createCell(row, 12, "LOẠI", colNameStyle);

        CellStyle dataStyle = workbook.createCellStyle();
        XSSFFont dataFont = workbook.createFont();
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
            for (int i = 0; i < saleDeli.size(); i++) {
                Row rowContent = sheet.createRow(start);
                SaleByDeliveryTypeDTO record = saleDeli.get(i);
                createCell(rowContent, 0, i + 1, dataStyle);
                createCell(rowContent, 1, record.getShopCode(), dataStyle);
                createCell(rowContent, 2, record.getShopName(), dataStyle);
                createCell(rowContent, 3, record.getCustomerCode(), dataStyle);
                createCell(rowContent, 4, record.getCustomerName(), dataStyle);
                createCell(rowContent, 5, record.getCustomerAddress(), dataStyle);
                createCell(rowContent, 6, record.getOrderNumber(), dataStyle);
                createCell(rowContent, 7, record.getAmount(), dataStyle2);
                createCell(rowContent, 8, record.getTotal(), dataStyle2);
                createCell(rowContent, 9, parseToStringDate(record.getOrderDate()), dataStyle);
                createCell(rowContent, 10, record.getDeliveryType(), dataStyle);
                createCell(rowContent, 11, record.getOnlineNumber(), dataStyle);
                createCell(rowContent, 12, record.getType(), dataStyle);
                start++;
            }
        }
    }

        private void createCell (Row row,int columnCount, Object value, CellStyle style){
            sheet.autoSizeColumn(columnCount);
            Cell cell = row.createCell(columnCount);
            if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof Float) {
                cell.setCellValue((Float) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Timestamp) {
                cell.setCellValue((Timestamp) value);
            } else {
                cell.setCellValue((String) value);
            }
            cell.setCellStyle(style);
        }

        public void setFromDate (Date fromDate){
            this.fromDate = fromDate;
        }

        public void setToDate (Date toDate){
            this.toDate = toDate;
        }

        public String parseToStringDate (Date date){
            Calendar c = Calendar.getInstance();
            if (date == null) return null;
            c.setTime(date);
            String day = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH) + "";
            String month = c.get(Calendar.MONTH) + 1 < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1) + "";
            String year = c.get(Calendar.YEAR) + "";
            return day + "/" + month + "/" + year;
        }

        public ByteArrayInputStream export () throws IOException {
            this.writeHeaderLine();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
}
