package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.dto.PromotionProductDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PromotionProductExcel {

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;
    private XSSFSheet sheet2;
    private XSSFSheet sheet3;

    private ShopDTO shop;
    private ShopDTO parentShop;
    private List<PromotionProductDTO> promotionProducts;
    private PromotionProductDTO promotionProductTotal;
    PromotionProductFilter filter;

    Map<String, CellStyle> style;
    public  PromotionProductExcel(ShopDTO shopDTO, ShopDTO parentShop, List<PromotionProductDTO> promotionProducts,
                                  PromotionProductDTO total, PromotionProductFilter filter) {
        this.shop = shopDTO;
        this.parentShop = parentShop;
        this.promotionProducts = promotionProducts;
        this.promotionProductTotal = total;
        this.filter = filter;
        style = ExcelPoiUtils.createStyles(workbook);
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
                    + this.parseToStringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + this.parseToStringDate(filter.getToDate()),style.get(ExcelPoiUtils.ITALIC_12));
        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 1, "NGÀY BÁN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 2, "NGÀNH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 3, "MÃ HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 4, "HÓA ĐƠN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 5, "SL", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 6, "GIÁ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 7, "THÀNH TiỀN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 8, "BARCODE", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 9, "TÊN HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 10, "ĐVT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 11, "MÃ CTKM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 12, "SỐ ĐƠN ONLINE", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 13, "LOẠI", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        if(!promotionProducts.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);
            createCell(sheet1, rowTotalHeader, 5, this.promotionProductTotal.getQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 7, this.promotionProductTotal.getTotalPrice(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalHeader, 8, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 10, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 11, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 12, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 13, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));

            for (int i = 0; i < promotionProducts.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                PromotionProductDTO record = promotionProducts.get(i);

                createCell(sheet1, rowValue, column++, i + 1, style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, this.parseToStringDate(record.getOrderDate()), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getProductCatName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getProductCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getOrderNumber(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getQuantity(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getPrice(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getTotalPrice(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet1, rowValue, column++, record.getBarCode(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet1, rowValue, column++, record.getProductName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getUom(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getPromotionCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getOnlineNumber(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getOrderType(), style.get(ExcelPoiUtils.DATA));
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);
            createCell(sheet1, rowTotalFooter, 5, this.promotionProductTotal.getQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 7, this.promotionProductTotal.getTotalPrice(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalFooter, 8, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 10, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 11, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 12, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 13, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        }

    }

    private void createTableSheet2() {
        int rowTable = 8;

        Row rowValues = sheet2.createRow(rowTable++);
        createCell(sheet2, rowValues, 0, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 1, "NGÀY BÁN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 2, "NGÀNH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 3, "MÃ HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 4, "BAR_CODE", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 5, "TÊN HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 6, "TÊN IN HĐ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 7, "ĐVT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 8, "SL", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 9, "GIÁ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet2, rowValues, 10, "THÀNH TIỀN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        if(!promotionProducts.isEmpty()) {
            Row rowTotalHeader = sheet2.createRow(rowTable++);
            createCell(sheet1, rowTotalHeader, 5, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 7, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 8, this.promotionProductTotal.getQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 10, this.promotionProductTotal.getTotalPrice(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));

            for (int i = 0; i < promotionProducts.size(); i++) {
                int column = 0;
                Row rowValue = sheet2.createRow(rowTable++);
                PromotionProductDTO record = promotionProducts.get(i);

                createCell(sheet2, rowValue, column++, i + 1, style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, this.parseToStringDate(record.getOrderDate()), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getProductCatName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getProductCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getBarCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getProductName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getProductName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getUom(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getQuantity(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet2, rowValue, column++, record.getPrice(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet2, rowValue, column++, record.getTotalPrice(), style.get(ExcelPoiUtils.DATA_CURRENCY));
            }

            Row rowTotalFooter = sheet2.createRow(rowTable++);
            createCell(sheet1, rowTotalFooter, 5, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 7, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 8, this.promotionProductTotal.getQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 10, this.promotionProductTotal.getTotalPrice(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
        }
    }

    private void createTableSheet3() {
        int rowTable = 8;
        Row rowValues = sheet3.createRow(rowTable++);
        createCell(sheet3, rowValues, 0, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 1, "NGÀNH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 2, "MÃ HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 3, "BAR_CODE", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 4, "TÊN HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 5, "TÊN IN HĐ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 6, "ĐVT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 7, "SL", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 8, "GIÁ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet3, rowValues, 9, "THÀNH TIỀN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        if(!promotionProducts.isEmpty()) {
            Row rowTotalHeader = sheet3.createRow(rowTable++);
            createCell(sheet3, rowTotalHeader, 5, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalHeader, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalHeader, 7, this.promotionProductTotal.getQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalHeader, 8, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalHeader, 9, this.promotionProductTotal.getTotalPrice(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));

            for (int i = 0; i < promotionProducts.size(); i++) {
                int column = 0;
                Row rowValue = sheet3.createRow(rowTable++);
                PromotionProductDTO record = promotionProducts.get(i);

                createCell(sheet3, rowValue, column++, i + 1, style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getProductCatName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getProductCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getBarCode(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getProductName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getProductName(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getUom(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getQuantity(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet3, rowValue, column++, record.getPrice(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet3, rowValue, column++, record.getTotalPrice(), style.get(ExcelPoiUtils.DATA_CURRENCY));
            }

            Row rowTotalFooter = sheet3.createRow(rowTable++);
            createCell(sheet3, rowTotalFooter, 5, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalFooter, 6, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalFooter, 7, this.promotionProductTotal.getQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalFooter, 8, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet3, rowTotalFooter, 9, this.promotionProductTotal.getTotalPrice(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
        }
    }

    private String parseToStringDate(Date date) {
        if(date == null) return null;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
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
