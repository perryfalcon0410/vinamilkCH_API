package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.messaging.EntryMenuDetailsReportsFilter;
import vn.viettel.report.messaging.SellsReportsFilter;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.SellDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SellExcel {
    private static final String FONT_NAME = "Times New Roman";

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;

    private ShopDTO shopDTO;
    private List<SellDTO> sellDTOS;
    private SellDTO sellDTO;
    SellsReportsFilter filter;

    Map<String, CellStyle> style;

    public SellExcel(
            ShopDTO shopDTO, List<SellDTO> sellDTOS, SellDTO total, SellsReportsFilter filter) {
        this.shopDTO = shopDTO;
        this.sellDTOS = sellDTOS;
        this.sellDTO = total;

        this.filter = filter;
        style = ExcelPoiUtils.createStyles(workbook);
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
                    + this.parseToStringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + this.parseToStringDate(filter.getToDate()), style.get(ExcelPoiUtils.ITALIC_12));

        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 1, "MÃ HÓA ĐƠN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 2, "MÃ KHÁCH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 3, "HỌ TÊN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 4, "ĐIỆN THOẠI", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 5, "NGÀNH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 6, "MÃ SẢN PHẨM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 7, "TÊN SẢN PHẨM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 8, "ĐVT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 9, "NGÀY BÁN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 10, "SỐ LƯỢNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 11, "GIÁ BÁN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 12, "TỔNG CỘNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 13, "KHUYẾN MÃI(TRƯỚC VAT)", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 14, "THANH TOÁN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 15, "GHI CHÚ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 16, "MÃ NHÂN VIÊN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 17, "HỌ VÀ TÊN NHÂN VIÊN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 18, "CỬA HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 19, "NHÓM SẢN PHẨM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 20, "SỐ ĐƠN ONLINE", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 21, "LOẠI", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));


        if (!sellDTOS.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalHeader, 4, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 5, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 7, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 8, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 10, this.sellDTO.getTotalQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 11, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 12, this.sellDTO.getTotalTotal(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalHeader, 13, this.sellDTO.getTotalPromotion(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalHeader, 14, this.sellDTO.getTotalPay(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalHeader, 15, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 16, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 17, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 18, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 19, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 20, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 21, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));


            for (int i = 0; i < sellDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                SellDTO record = sellDTOS.get(i);

                createCell(sheet1, rowValue, column++, i + 1, style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getOrderNumber(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getCustomerCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getCustomerName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getNumberPhone(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getIndustry(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getProductCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getProductName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getUnit(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, this.parseToStringDateTime(record.getOrderDate()), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getQuantity(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getPrice(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getTotal(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet1, rowValue, column++, record.getPromotion(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet1, rowValue, column++, record.getPay(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet1, rowValue, column++, record.getNote(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet1, rowValue, column++, record.getEmployeeCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getEmployeeName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, shopDTO.getShopName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getProductGroups(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getOnlineNumber(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getSalesChannel(), style.get(ExcelPoiUtils.DATA));
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalFooter, 4, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 5, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 7, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 8, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 10, this.sellDTO.getTotalQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 11, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 12, this.sellDTO.getTotalTotal(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalFooter, 13, this.sellDTO.getTotalPromotion(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalFooter, 14, this.sellDTO.getTotalPay(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalFooter, 15, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 16, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 17, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 18, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 19, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 20, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 21, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));

        }

    }

    private String parseToStringDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    private String parseToStringDateTime(Timestamp date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        return dateFormat.format(date);
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
