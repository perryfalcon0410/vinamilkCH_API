package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.SellsReportsRequest;
import vn.viettel.report.service.dto.SellDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellExcel {
    private static final String FONT_NAME = "Times New Roman";

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;

    private ShopDTO shopDTO;
    private List<SellDTO> sellDTOS;
    private SellDTO sellDTO;
    SellsReportsRequest filter;

    Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);;
    CellStyle format = style.get(ExcelPoiUtils.DATA);
    CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
    CellStyle format2 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
    CellStyle format3 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY);
    CellStyle format4 = style.get(ExcelPoiUtils.DATA_CURRENCY);

    public SellExcel(
            ShopDTO shopDTO, List<SellDTO> sellDTOS, SellDTO total, SellsReportsRequest filter) {
        this.shopDTO = shopDTO;
        this.sellDTOS = sellDTOS;
        this.sellDTO = total;
        this.filter = filter;
    }

    private void writeHeaderLine() {


        List<XSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("sheet1");
        sheets.add(sheet1);


        for (XSSFSheet sheet : sheets) {
            int col = 0, row = 0, colm = 9, rowm = 0;

            ExcelPoiUtils.addCellsAndMerged(sheet, col, row, colm, rowm, shopDTO.getShopName(), style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, shopDTO.getAddress(), style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, "Tel:" + " " + shopDTO.getPhone() + "  " + "Fax:" + " " + shopDTO.getFax(), style.get(ExcelPoiUtils.HEADER_LEFT));
            //header right
            ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 2, colm + 9, rowm - 2, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 1, colm + 9, rowm - 1, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row, colm + 9, rowm, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style.get(ExcelPoiUtils.HEADER_LEFT));

            ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 3, colm + 15, rowm + 3, "BÁO CÁO BÁN HÀNG ", style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 5, colm + 15, rowm + 5, "TỪ NGÀY: "
                    + DateUtils.formatDate2StringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(filter.getToDate()), style.get(ExcelPoiUtils.ITALIC_12));

        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", format1);
        createCell(sheet1, rowHeader, 1, "MÃ HÓA ĐƠN", format1);
        createCell(sheet1, rowHeader, 2, "MÃ KHÁCH HÀNG", format1);
        createCell(sheet1, rowHeader, 3, "HỌ TÊN", format1);
        createCell(sheet1, rowHeader, 4, "ĐIỆN THOẠI", format1);
        createCell(sheet1, rowHeader, 5, "NGÀNH HÀNG", format1);
        createCell(sheet1, rowHeader, 6, "MÃ SẢN PHẨM", format1);
        createCell(sheet1, rowHeader, 7, "TÊN SẢN PHẨM", format1);
        createCell(sheet1, rowHeader, 8, "ĐVT", format1);
        createCell(sheet1, rowHeader, 9, "NGÀY BÁN", format1);
        createCell(sheet1, rowHeader, 10, "SỐ LƯỢNG", format1);
        createCell(sheet1, rowHeader, 11, "GIÁ BÁN", format1);
        createCell(sheet1, rowHeader, 12, "TỔNG CỘNG", format1);
        createCell(sheet1, rowHeader, 13, "KHUYẾN MÃI(TRƯỚC VAT)", format1);
        createCell(sheet1, rowHeader, 14, "THANH TOÁN", format1);
        createCell(sheet1, rowHeader, 15, "GHI CHÚ", format1);
        createCell(sheet1, rowHeader, 16, "MÃ NHÂN VIÊN", format1);
        createCell(sheet1, rowHeader, 17, "HỌ VÀ TÊN NHÂN VIÊN", format1);
        createCell(sheet1, rowHeader, 18, "CỬA HÀNG", format1);
        createCell(sheet1, rowHeader, 19, "NHÓM SẢN PHẨM", format1);
        createCell(sheet1, rowHeader, 20, "SỐ ĐƠN ONLINE", format1);
        createCell(sheet1, rowHeader, 21, "LOẠI", format1);


        if (!sellDTOS.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalHeader, 4, "Tổng:", format2);
            createCell(sheet1, rowTotalHeader, 5, null, format2);
            createCell(sheet1, rowTotalHeader, 6, null, format2);
            createCell(sheet1, rowTotalHeader, 7, null, format2);
            createCell(sheet1, rowTotalHeader, 8, null, format2);
            createCell(sheet1, rowTotalHeader, 9, null, format2);
            createCell(sheet1, rowTotalHeader, 10, this.sellDTO.getTotalQuantity(), format2);
            createCell(sheet1, rowTotalHeader, 11, null, format2);
            createCell(sheet1, rowTotalHeader, 12, this.sellDTO.getTotalTotal(), format3);
            createCell(sheet1, rowTotalHeader, 13, this.sellDTO.getTotalPromotion(), format3);
            createCell(sheet1, rowTotalHeader, 14, this.sellDTO.getTotalPay(), format3);
            createCell(sheet1, rowTotalHeader, 15, null, format2);
            createCell(sheet1, rowTotalHeader, 16, null, format2);
            createCell(sheet1, rowTotalHeader, 17, null, format2);
            createCell(sheet1, rowTotalHeader, 18, null, format2);
            createCell(sheet1, rowTotalHeader, 19, null, format2);
            createCell(sheet1, rowTotalHeader, 20, null, format2);
            createCell(sheet1, rowTotalHeader, 21, null, format2);


            for (int i = 0; i < sellDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                SellDTO record = sellDTOS.get(i);

                createCell(sheet1, rowValue, column++, i + 1, format);
                createCell(sheet1, rowValue, column++, record.getOrderNumber(), format);
                createCell(sheet1, rowValue, column++, record.getCustomerCode(), format);
                createCell(sheet1, rowValue, column++, record.getCustomerName(), format);
                createCell(sheet1, rowValue, column++, record.getNumberPhone(), format);
                createCell(sheet1, rowValue, column++, record.getIndustry(), format);
                createCell(sheet1, rowValue, column++, record.getProductCode(), format);
                createCell(sheet1, rowValue, column++, record.getProductName(), format);
                createCell(sheet1, rowValue, column++, record.getUnit(), format);
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDateTime(record.getOrderDate()), format);
                createCell(sheet1, rowValue, column++, record.getQuantity(), format);
                createCell(sheet1, rowValue, column++, record.getPrice(), format);
                createCell(sheet1, rowValue, column++, record.getTotal(), format4);
                createCell(sheet1, rowValue, column++, record.getPromotion(), format4);
                createCell(sheet1, rowValue, column++, record.getPay(), format4);
                createCell(sheet1, rowValue, column++, record.getNote(), format4);
                createCell(sheet1, rowValue, column++, record.getEmployeeCode(), format);
                createCell(sheet1, rowValue, column++, record.getEmployeeName(), format);
                createCell(sheet1, rowValue, column++, shopDTO.getShopName(), format);
                createCell(sheet1, rowValue, column++, record.getProductGroups(), format);
                createCell(sheet1, rowValue, column++, record.getOnlineNumber(), format);
                createCell(sheet1, rowValue, column++, record.getSalesChannel(), format);
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalFooter, 4, "Tổng:", format2);
            createCell(sheet1, rowTotalFooter, 5, null, format2);
            createCell(sheet1, rowTotalFooter, 6, null, format2);
            createCell(sheet1, rowTotalFooter, 7, null, format2);
            createCell(sheet1, rowTotalFooter, 8, null, format2);
            createCell(sheet1, rowTotalFooter, 9, null, format2);
            createCell(sheet1, rowTotalFooter, 10, this.sellDTO.getTotalQuantity(), format2);
            createCell(sheet1, rowTotalFooter, 11, null, format2);
            createCell(sheet1, rowTotalFooter, 12, this.sellDTO.getTotalTotal(), format3);
            createCell(sheet1, rowTotalFooter, 13, this.sellDTO.getTotalPromotion(), format3);
            createCell(sheet1, rowTotalFooter, 14, this.sellDTO.getTotalPay(), format3);
            createCell(sheet1, rowTotalFooter, 15, null, format2);
            createCell(sheet1, rowTotalFooter, 16, null, format2);
            createCell(sheet1, rowTotalFooter, 17, null, format2);
            createCell(sheet1, rowTotalFooter, 18, null, format2);
            createCell(sheet1, rowTotalFooter, 19, null, format2);
            createCell(sheet1, rowTotalFooter, 20, null, format2);
            createCell(sheet1, rowTotalFooter, 21, null, format2);

        }

    }

    private void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }


    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
