package vn.viettel.report.service.excel;

import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.dto.PromotionProductDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
public class PromotionProductExcel {

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;
    private XSSFSheet sheet2;
    private XSSFSheet sheet3;

    private ShopDTO shop;
    private ShopDTO parentShop;
    private List<PromotionProductDTO> promotionDetails;
    private List<PromotionProductDTO> promotionIndays;
    private List<PromotionProductDTO> promotionproducts;
    private PromotionProductDTO promotionProductTotal;
    PromotionProductFilter filter;
    Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
    CellStyle format = style.get(ExcelPoiUtils.DATA);
    CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
    CellStyle format2 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
    CellStyle format4 = style.get(ExcelPoiUtils.DATA_CURRENCY);

    public PromotionProductExcel(ShopDTO shopDTO, ShopDTO parentShop, PromotionProductDTO total, PromotionProductFilter filter) {
        this.shop = shopDTO;
        this.parentShop = parentShop;
        this.promotionProductTotal = total;
        this.filter = filter;
    }

    private void writeHeaderLine()  {

        List<XSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("KM_ChiTiet");
        sheet2 = workbook.createSheet("KM_TheoNgay");
        sheet3 = workbook.createSheet("KM_TheoSP");
        sheets.add(sheet1);
        sheets.add(sheet2);
        sheets.add(sheet3);

        for(XSSFSheet sheet: sheets) {
            int col = 0,row =0, colm = 9, rowm =0;
            ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
            //header right
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel:"+" "+parentShop.getPhone()+"  "+"Fax:"+" "+parentShop.getFax(),style.get(ExcelPoiUtils.HEADER_LEFT));

            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO HÀNG KHUYẾN MÃI",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "
                    + DateUtils.formatDate2StringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(filter.getToDate()),style.get(ExcelPoiUtils.ITALIC_12));
        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", format1);
        createCell(sheet1, rowHeader, 1, "NGÀY BÁN", format1);
        createCell(sheet1, rowHeader, 2, "NGÀNH HÀNG", format1);
        createCell(sheet1, rowHeader, 3, "MÃ HÀNG", format1);
        createCell(sheet1, rowHeader, 4, "HÓA ĐƠN", format1);
        createCell(sheet1, rowHeader, 5, "SL", format1);
        createCell(sheet1, rowHeader, 6, "BARCODE", format1);
        createCell(sheet1, rowHeader, 7, "TÊN HÀNG", format1);
        createCell(sheet1, rowHeader, 8, "ĐVT", format1);
        createCell(sheet1, rowHeader, 9, "MÃ CTKM", format1);
        createCell(sheet1, rowHeader, 10, "SỐ ĐƠN ONLINE", format1);
        createCell(sheet1, rowHeader, 11, "LOẠI", format1);

        if(!promotionDetails.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);
            createCell(sheet1, rowTotalHeader, 5, this.promotionProductTotal.getQuantity(), format2);
            createCell(sheet1, rowTotalHeader, 6, null, format2);
            createCell(sheet1, rowTotalHeader, 7, null, format2);
            createCell(sheet1, rowTotalHeader, 8, null, format2);
            createCell(sheet1, rowTotalHeader, 9, null, format2);
            createCell(sheet1, rowTotalHeader, 10, null, format2);
            createCell(sheet1, rowTotalHeader, 11, null, format2);

            for (int i = 0; i < promotionDetails.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                PromotionProductDTO record = promotionDetails.get(i);

                createCell(sheet1, rowValue, column++, i + 1, format);
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getOrderDate()), format);
                createCell(sheet1, rowValue, column++, record.getProductCatName(), format);
                createCell(sheet1, rowValue, column++, record.getProductCode(), format);
                createCell(sheet1, rowValue, column++, record.getOrderNumber(), format);
                createCell(sheet1, rowValue, column++, record.getQuantity(), format);
                createCell(sheet1, rowValue, column++, record.getBarCode(), format4);
                createCell(sheet1, rowValue, column++, record.getProductName(), format);
                createCell(sheet1, rowValue, column++, record.getUom(), format);
                createCell(sheet1, rowValue, column++, record.getPromotionCode(), format);
                createCell(sheet1, rowValue, column++, record.getOnlineNumber(), format);
                createCell(sheet1, rowValue, column++, record.getOrderType(), format);
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);
            createCell(sheet1, rowTotalFooter, 5, this.promotionProductTotal.getQuantity(), format2);
            createCell(sheet1, rowTotalFooter, 6, null, format2);
            createCell(sheet1, rowTotalFooter, 7, null, format2);
            createCell(sheet1, rowTotalFooter, 8, null, format2);
            createCell(sheet1, rowTotalFooter, 9, null, format2);
            createCell(sheet1, rowTotalFooter, 10, null, format2);
            createCell(sheet1, rowTotalFooter, 11, null, format2);
        }

    }

    private void createTableSheet2() {
        int rowTable = 8;

        Row rowValues = sheet2.createRow(rowTable++);
        createCell(sheet2, rowValues, 0, "STT", format1);
        createCell(sheet2, rowValues, 1, "NGÀY BÁN", format1);
        createCell(sheet2, rowValues, 2, "NGÀNH HÀNG", format1);
        createCell(sheet2, rowValues, 3, "MÃ HÀNG", format1);
        createCell(sheet2, rowValues, 4, "BAR_CODE", format1);
        createCell(sheet2, rowValues, 5, "TÊN HÀNG", format1);
        createCell(sheet2, rowValues, 6, "TÊN IN HĐ", format1);
        createCell(sheet2, rowValues, 7, "ĐVT", format1);
        createCell(sheet2, rowValues, 8, "SL", format1);

        if(!promotionIndays.isEmpty()) {
            Row rowTotalHeader = sheet2.createRow(rowTable++);
            createCell(sheet1, rowTotalHeader, 5, "Tổng:", format2);
            createCell(sheet1, rowTotalHeader, 6, null, format2);
            createCell(sheet1, rowTotalHeader, 7, null, format2);
            createCell(sheet1, rowTotalHeader, 8, this.promotionProductTotal.getQuantity(), format2);

            for (int i = 0; i < promotionIndays.size(); i++) {
                int column = 0;
                Row rowValue = sheet2.createRow(rowTable++);
                PromotionProductDTO record = promotionIndays.get(i);

                createCell(sheet2, rowValue, column++, i + 1, format);
                createCell(sheet2, rowValue, column++, DateUtils.formatDate2StringDate(record.getOrderDate()), format);
                createCell(sheet2, rowValue, column++, record.getProductCatName(), format);
                createCell(sheet2, rowValue, column++, record.getProductCode(), format);
                createCell(sheet2, rowValue, column++, record.getBarCode(), format);
                createCell(sheet2, rowValue, column++, record.getProductName(), format);
                createCell(sheet2, rowValue, column++, record.getProductName(), format);
                createCell(sheet2, rowValue, column++, record.getUom(), format);
                createCell(sheet2, rowValue, column++, record.getQuantity(), format);
            }

            Row rowTotalFooter = sheet2.createRow(rowTable++);
            createCell(sheet1, rowTotalFooter, 5, "Tổng:", format2);
            createCell(sheet1, rowTotalFooter, 6, null, format2);
            createCell(sheet1, rowTotalFooter, 7, null, format2);
            createCell(sheet1, rowTotalFooter, 8, this.promotionProductTotal.getQuantity(), format2);
        }
    }

    private void createTableSheet3() {
        int rowTable = 8;
        Row rowValues = sheet3.createRow(rowTable++);
        createCell(sheet3, rowValues, 0, "STT", format1);
        createCell(sheet3, rowValues, 1, "NGÀNH HÀNG", format1);
        createCell(sheet3, rowValues, 2, "MÃ HÀNG", format1);
        createCell(sheet3, rowValues, 3, "BAR_CODE", format1);
        createCell(sheet3, rowValues, 4, "TÊN HÀNG", format1);
        createCell(sheet3, rowValues, 5, "TÊN IN HĐ", format1);
        createCell(sheet3, rowValues, 6, "ĐVT", format1);
        createCell(sheet3, rowValues, 7, "SL", format1);

        if(!promotionproducts.isEmpty()) {
            Row rowTotalHeader = sheet3.createRow(rowTable++);
            createCell(sheet3, rowTotalHeader, 5, "Tổng:", format2);
            createCell(sheet3, rowTotalHeader, 6, null, format2);
            createCell(sheet3, rowTotalHeader, 7, this.promotionProductTotal.getQuantity(), format2);


            for (int i = 0; i < promotionproducts.size(); i++) {
                int column = 0;
                Row rowValue = sheet3.createRow(rowTable++);
                PromotionProductDTO record = promotionproducts.get(i);

                createCell(sheet3, rowValue, column++, i + 1, format);
                createCell(sheet3, rowValue, column++, record.getProductCatName(), format);
                createCell(sheet3, rowValue, column++, record.getProductCode(), format);
                createCell(sheet3, rowValue, column++, record.getBarCode(), format);
                createCell(sheet3, rowValue, column++, record.getProductName(), format);
                createCell(sheet3, rowValue, column++, record.getProductName(), format);
                createCell(sheet3, rowValue, column++, record.getUom(), format);
                createCell(sheet3, rowValue, column++, record.getQuantity(), format);
            }

            Row rowTotalFooter = sheet3.createRow(rowTable++);
            createCell(sheet3, rowTotalFooter, 5, "Tổng:", format2);
            createCell(sheet3, rowTotalFooter, 6, null, format2);
            createCell(sheet3, rowTotalFooter, 7, this.promotionProductTotal.getQuantity(), format2);
        }
    }

    private void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {

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
        sheet.autoSizeColumn(columnCount);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        this.createTableSheet2();
        this.createTableSheet3();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
