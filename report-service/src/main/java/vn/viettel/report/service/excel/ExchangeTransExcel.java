package vn.viettel.report.service.excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.dto.ExchangeTransReportRate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExchangeTransExcel {
    private ShopDTO shopDTO;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ExchangeTransReportDTO> exchangeTransList;
    private List<ExchangeTransReportRate> totalRate;
    private ExchangeTransReportDTO exchangeTransTotal;
    private Date fromDate;
    private Date toDate;

    public ExchangeTransExcel(ShopDTO shopDTO, List<ExchangeTransReportDTO> exchangeTransList, ExchangeTransReportDTO total, List<ExchangeTransReportRate> totalRate) {
        workbook = new XSSFWorkbook();
        {
            this.shopDTO = shopDTO;
            this.exchangeTransList = exchangeTransList;
            this.exchangeTransTotal = total;
            this.totalRate = totalRate;
        }
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Hàng_hỏng");
        ////////// HEADER /////////////////////////////
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H1:M1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("B15:C15"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("B16:C16"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("B17:C17"));
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

        createCell(customerRow, 0,shopDTO.getShopName(),headerStyle);
        createCell(customerRow, 7, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM",headerStyle);
        createCell(customerAddressRow, 0,shopDTO.getAddress(),addressStyle);
        createCell(customerAddressRow, 7, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",addressStyle);
        createCell(customerPhoneRow, 0, "Tel: " + shopDTO.getMobiPhone(),addressStyle);
        createCell(customerPhoneRow, 7, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",addressStyle);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:L6"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A8:L8"));
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

        createCell(header, 0, "BẢNG TỔNG HỢP ĐỔI HÀNG HƯ HỎNG", titleStyle);
        createCell(dateRow, 0, "TỪ NGÀY: " +
                this.parseToStringDate(fromDate) + "   ĐẾN NGÀY: " + this.parseToStringDate(toDate), dateStyle);
        createCell(row, 0, "STT", colNameStyle);
        createCell(row, 1, "NGÀY BIÊN BẢN", colNameStyle);
        createCell(row, 2, "SỐ BIÊN BẢN", colNameStyle);
        createCell(row, 3, "MÃ KHÁCH HÀNG", colNameStyle);
        createCell(row, 4, "TÊN KHÁCH HÀNG", colNameStyle);
        createCell(row, 5, "ĐỊA CHỈ", colNameStyle);
        createCell(row, 6, "MÃ SẢN PHẨM", colNameStyle);
        createCell(row, 7, "TÊN SẢN PHẨM", colNameStyle);
        createCell(row, 8, "SỐ LƯỢNG", colNameStyle);
        createCell(row, 9, "THÀNH TIỀN", colNameStyle);
        createCell(row, 10, "LÍ DO", colNameStyle);
        createCell(row, 11, "SỐ ĐT", colNameStyle);

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

        if(!exchangeTransList.isEmpty()){
            int start = 9;
            for(int i = 0; i<exchangeTransList.size(); i++){
                Row rowContent = sheet.createRow(start);
                ExchangeTransReportDTO record = exchangeTransList.get(i);
                createCell(rowContent, 0, i + 1, dataStyle);
                createCell(rowContent, 1, this.parseToStringDate(record.getTransDate()), dataStyle);
                createCell(rowContent, 2, record.getTransNumber(), dataStyle);
                createCell(rowContent, 3, record.getCustomerCode(), dataStyle);
                createCell(rowContent, 4, record.getCustomerName(), dataStyle);
                createCell(rowContent, 5, record.getAddress(), dataStyle);
                createCell(rowContent, 6, record.getProductCode(), dataStyle);
                createCell(rowContent, 7, record.getProductName(), dataStyle);
                createCell(rowContent, 8, record.getQuantity(), dataStyle);
                createCell(rowContent, 9, record.getAmount(), dataStyle2);
                createCell(rowContent, 10, record.getCategoryName(), dataStyle);
                createCell(rowContent, 11, record.getPhone(), dataStyle);
                start++;
            }

            CellStyle totalRowStyle = workbook.createCellStyle();
            XSSFFont fontTotal = workbook.createFont();
            fontTotal.setFontHeight(10);
            fontTotal.setFontName("Times New Roman");
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
            Row totalRowFooter = sheet.createRow(9 + exchangeTransList.size());
            createCell(totalRowFooter, 1, "Tổng cộng: ", totalRowStyleRGB);
            createCell(totalRowFooter, 8, this.exchangeTransTotal.getQuantity(), totalRowStyleRGB);
            createCell(totalRowFooter, 2, null, totalRowStyleRGB);
            createCell(totalRowFooter, 3, null, totalRowStyleRGB);
            createCell(totalRowFooter, 4, null, totalRowStyleRGB);
            createCell(totalRowFooter, 5, null, totalRowStyleRGB);
            createCell(totalRowFooter, 6, null, totalRowStyleRGB);
            createCell(totalRowFooter, 7, null, totalRowStyleRGB);
            createCell(totalRowFooter, 9, null, totalRowStyleRGB);
            createCell(totalRowFooter, 10, null, totalRowStyleRGB);
            createCell(totalRowFooter, 11, null, totalRowStyleRGB);

            if(!totalRate.isEmpty()) {
                Row salesRow = sheet.createRow(13 + exchangeTransList.size());
                createCell(salesRow, 1, "Doanh số", dataStyle);
                Row rateRow = sheet.createRow(14 + exchangeTransList.size());
                createCell(rateRow, 1, "Định mức đổi hàng", dataStyle);
                Row recommendRow = sheet.createRow(15 + exchangeTransList.size());
                createCell(recommendRow, 1, "Số tiền đề nghị duyệt", dataStyle);
                for(int i = 0; i<totalRate.size(); i++){
                    ExchangeTransReportRate record = totalRate.get(i);
                    createCell(salesRow, 3, record.getTotalSale(),dataStyle);
                    createCell(rateRow, 3, record.getExchangeRate(),dataStyle);
                }
            }
        }
    }
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if(value instanceof Float) {
            cell.setCellValue((Float)value);
        }else if(value instanceof Double) {
            cell.setCellValue((Double) value);
        }else if(value instanceof Long) {
            cell.setCellValue((Long) value);
        }else if(value instanceof Timestamp) {
            cell.setCellValue((Timestamp) value);
        }
        else{
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
