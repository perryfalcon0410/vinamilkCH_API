package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

    private SXSSFWorkbook workbook = new SXSSFWorkbook();
    private SXSSFSheet sheet1;

    private ShopDTO shopDTO;
    private ShopDTO parentShop;
    private List<SellDTO> sellDTOS;
    private SellDTO sellDTO;
    private SellsReportsRequest filter;

    private Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);;
    private CellStyle format = style.get(ExcelPoiUtils.DATA);
    private CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
    private CellStyle format2 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
    private CellStyle format3 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY);
    private CellStyle format4 = style.get(ExcelPoiUtils.DATA_CURRENCY);

    public SellExcel(ShopDTO shopDTO, ShopDTO parentShop, List<SellDTO> sellDTOS, SellDTO total, SellsReportsRequest filter) {
        this.shopDTO = shopDTO;
        this.parentShop = parentShop;
        this.sellDTOS = sellDTOS;
        this.sellDTO = total;
        this.filter = filter;
    }

    private void writeHeaderLine() {
        List<SXSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("sheet1");
        sheets.add(sheet1);

        for (SXSSFSheet sheet : sheets) {
            int col = 0, row = 0, colm = 9, rowm = 0;

            ExcelPoiUtils.addCellsAndMerged(sheet, col, row, colm, rowm, shopDTO.getShopName(), style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, shopDTO.getAddress(), style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, "Tel: " + (shopDTO.getPhone()!=null? shopDTO.getPhone():"") + " Fax: " + (shopDTO.getFax()!=null?shopDTO.getFax():""), style.get(ExcelPoiUtils.HEADER_LEFT));
            //header right
            if(parentShop != null) {
                ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 2, colm + 9, rowm - 2, parentShop.getShopName(), style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
                ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 1, colm + 9, rowm - 1, parentShop.getAddress(), style.get(ExcelPoiUtils.HEADER_LEFT));
                ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row, colm + 9, rowm, "Tel: " + (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""), style.get(ExcelPoiUtils.HEADER_LEFT));
            }

            ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 3, colm + 15, rowm + 3, "BÁO CÁO BÁN HÀNG ", style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 5, colm + 15, rowm + 5, "TỪ NGÀY: "
                    + DateUtils.formatDate2StringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(filter.getToDate()), style.get(ExcelPoiUtils.ITALIC_12));
        }
    }

    private void createTableSheet1() {
        int rowTable = 8, lastCol = 0;

        Row rowHeader = sheet1.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowHeader, 0, "STT", format1);
        ExcelPoiUtils.createCell(rowHeader, 1, "MÃ HÓA ĐƠN", format1);
        ExcelPoiUtils.createCell(rowHeader, 2, "MÃ KHÁCH HÀNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 3, "HỌ TÊN", format1);
        ExcelPoiUtils.createCell(rowHeader, 4, "ĐIỆN THOẠI", format1);
        ExcelPoiUtils.createCell(rowHeader, 5, "NGÀNH HÀNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 6, "MÃ SẢN PHẨM", format1);
        ExcelPoiUtils.createCell(rowHeader, 7, "TÊN SẢN PHẨM", format1);
        ExcelPoiUtils.createCell(rowHeader, 8, "ĐVT", format1);
        ExcelPoiUtils.createCell(rowHeader, 9, "NGÀY BÁN", format1);
        ExcelPoiUtils.createCell(rowHeader, 10, "SỐ LƯỢNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 11, "GIÁ BÁN", format1);
        ExcelPoiUtils.createCell(rowHeader, 12, "TỔNG CỘNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 13, "KHUYẾN MÃI(TRƯỚC VAT)", format1);
        ExcelPoiUtils.createCell(rowHeader, 14, "THANH TOÁN", format1);
        ExcelPoiUtils.createCell(rowHeader, 15, "GHI CHÚ", format1);
        ExcelPoiUtils.createCell(rowHeader, 16, "MÃ NHÂN VIÊN", format1);
        ExcelPoiUtils.createCell(rowHeader, 17, "HỌ VÀ TÊN NHÂN VIÊN", format1);
        ExcelPoiUtils.createCell(rowHeader, 18, "CỬA HÀNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 19, "NHÓM SẢN PHẨM", format1);
        ExcelPoiUtils.createCell(rowHeader, 20, "SỐ ĐƠN ONLINE", format1);
        ExcelPoiUtils.createCell(rowHeader, 21, "LOẠI", format1);
        ExcelPoiUtils.autoSizeAllColumns(sheet1, lastCol);

        if (!sellDTOS.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);

            ExcelPoiUtils.createCell(rowTotalHeader, 4, "Tổng:", format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 5, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 6, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 7, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 8, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 9, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 10, this.sellDTO.getTotalQuantity(), format3);
            ExcelPoiUtils.createCell(rowTotalHeader, 11, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 12, this.sellDTO.getTotalTotal(), format3);
            ExcelPoiUtils.createCell(rowTotalHeader, 13, this.sellDTO.getTotalPromotionNotVAT(), format3);
            ExcelPoiUtils.createCell(rowTotalHeader, 14, this.sellDTO.getTotalPay(), format3);
            ExcelPoiUtils.createCell(rowTotalHeader, 15, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 16, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 17, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 18, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 19, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 20, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 21, null, format2);

            for (int i = 0; i < sellDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                SellDTO record = sellDTOS.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderNumber(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getCustomerCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getCustomerName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getNumberPhone(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getIndustry(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getUnit(), format);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDateTime(record.getOrderDate()), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getQuantity(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPrice(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getTotal(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPromotionNotVAT(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPay(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getNote(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getEmployeeCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getEmployeeName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, shopDTO.getShopName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductGroups(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOnlineNumber(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getSalesChannel(), format);
                if(column > lastCol) lastCol = column;
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);

            ExcelPoiUtils.createCell(rowTotalFooter, 4, "Tổng:", format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 5, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 6, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 7, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 8, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 9, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 10, this.sellDTO.getTotalQuantity(), format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 11, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 12, this.sellDTO.getTotalTotal(), format3);
            ExcelPoiUtils.createCell(rowTotalFooter, 13, this.sellDTO.getTotalPromotionNotVAT(), format3);
            ExcelPoiUtils.createCell(rowTotalFooter, 14, this.sellDTO.getTotalPay(), format3);
            ExcelPoiUtils.createCell(rowTotalFooter, 15, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 16, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 17, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 18, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 19, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 20, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 21, null, format2);
        }

        ExcelPoiUtils.autoSizeAllColumns(sheet1, lastCol);
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
