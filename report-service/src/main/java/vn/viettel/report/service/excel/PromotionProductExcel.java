package vn.viettel.report.service.excel;

import lombok.Setter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
public class PromotionProductExcel extends ExcelPoiUtils{

    private SXSSFWorkbook workbook = new SXSSFWorkbook();
    private SXSSFSheet sheet1;
    private SXSSFSheet sheet2;
    private SXSSFSheet sheet3;

    private ShopDTO shop;
    private ShopDTO parentShop;
    private List<PromotionProductDTO> promotionDetails;
    private List<PromotionProductDTO> promotionIndays;
    private List<PromotionProductDTO> promotionproducts;
    private PromotionProductDTO promotionProductTotal;
    private PromotionProductFilter filter;
    private Map<String, CellStyle> style = this.createStyles(workbook);
    private CellStyle format = style.get(ExcelPoiUtils.DATA);
    private CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
    private CellStyle format2 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
    private CellStyle format4 = style.get(ExcelPoiUtils.DATA_CURRENCY);
    private CellStyle format5 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY);

    public PromotionProductExcel(ShopDTO shopDTO, ShopDTO parentShop, PromotionProductDTO total, PromotionProductFilter filter) {
        this.shop = shopDTO;
        this.parentShop = parentShop;
        this.promotionProductTotal = total;
        this.filter = filter;
    }

    private void writeHeaderLine()  {

        List<SXSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("KM_ChiTiet");
        sheet2 = workbook.createSheet("KM_TheoNgay");
        sheet3 = workbook.createSheet("KM_TheoSP");
        sheets.add(sheet1);
        sheets.add(sheet2);
        sheets.add(sheet3);

        for(SXSSFSheet sheet: sheets) {
            int col = 0,row =0, colm = 9, rowm =0;
            ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (shop.getPhone()!=null? shop.getPhone():"") + " Fax: " + (shop.getFax()!=null?shop.getFax():"") ,style.get(ExcelPoiUtils.HEADER_LEFT));
            //header right
            if(parentShop !=null) {
                ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
                ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
                ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: " +  (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));

            }

            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO HÀNG KHUYẾN MÃI",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "
                    + DateUtils.formatDate2StringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(filter.getToDate()),style.get(ExcelPoiUtils.ITALIC_12));
        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowHeader, 0, "STT", format1);
        ExcelPoiUtils.createCell(rowHeader, 1, "NGÀY BÁN", format1);
        ExcelPoiUtils.createCell(rowHeader, 2, "NGÀNH HÀNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 3, "MÃ HÀNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 4, "HÓA ĐƠN", format1);
        ExcelPoiUtils.createCell(rowHeader, 5, "SL", format1);
        ExcelPoiUtils.createCell(rowHeader, 6, "GIÁ", format1);
        ExcelPoiUtils.createCell(rowHeader, 7, "THÀNH TIỀN", format1);
        ExcelPoiUtils.createCell(rowHeader, 8, "BARCODE", format1);
        ExcelPoiUtils.createCell(rowHeader, 9, "TÊN HÀNG", format1);
        ExcelPoiUtils.createCell(rowHeader, 10, "ĐVT", format1);
        ExcelPoiUtils.createCell(rowHeader, 11, "MÃ CTKM", format1);
        ExcelPoiUtils.createCell(rowHeader, 12, "SỐ ĐƠN ONLINE", format1);
        ExcelPoiUtils.createCell(rowHeader, 13, "LOẠI", format1);

        if(!promotionDetails.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);
            ExcelPoiUtils.createCell(rowTotalHeader, 5, this.promotionProductTotal.getQuantity(), format5);
            ExcelPoiUtils.createCell(rowTotalHeader, 6, this.promotionProductTotal.getPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalHeader, 7, this.promotionProductTotal.getTotalPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalHeader, 8, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 9, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 10, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 11, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 12, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 13, null, format2);

            for (int i = 0; i < promotionDetails.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                PromotionProductDTO record = promotionDetails.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, format);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDate(record.getOrderDate()), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCatName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderNumber(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getQuantity(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPrice(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getTotalPrice(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getBarCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getUom(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPromotionCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOnlineNumber(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getOrderType(), format);

            }
            ExcelPoiUtils.autoSizeAllColumns(sheet1, 13);
            Row rowTotalFooter = sheet1.createRow(rowTable++);
            ExcelPoiUtils.createCell(rowTotalFooter, 5, this.promotionProductTotal.getQuantity(), format5);
            ExcelPoiUtils.createCell(rowTotalFooter, 6, this.promotionProductTotal.getPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalFooter, 7, this.promotionProductTotal.getTotalPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalFooter, 8, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 9, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 10, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 11, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 12, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 13, null, format2);

        }
    }

    private void createTableSheet2() {
        int rowTable = 8;

        Row rowValues = sheet2.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowValues, 0, "STT", format1);
        ExcelPoiUtils.createCell(rowValues, 1, "NGÀY BÁN", format1);
        ExcelPoiUtils.createCell(rowValues, 2, "NGÀNH HÀNG", format1);
        ExcelPoiUtils.createCell(rowValues, 3, "MÃ HÀNG", format1);
        ExcelPoiUtils.createCell(rowValues, 4, "BAR_CODE", format1);
        ExcelPoiUtils.createCell(rowValues, 5, "TÊN HÀNG", format1);
        ExcelPoiUtils.createCell(rowValues, 6, "TÊN IN HĐ", format1);
        ExcelPoiUtils.createCell(rowValues, 7, "ĐVT", format1);
        ExcelPoiUtils.createCell(rowValues, 8, "SL", format1);
        ExcelPoiUtils.createCell(rowValues, 9, "GIÁ", format1);
        ExcelPoiUtils.createCell(rowValues, 10, "THÀNH TIỀN", format1);

        if(!promotionIndays.isEmpty()) {
            Row rowTotalHeader = sheet2.createRow(rowTable++);
            ExcelPoiUtils.createCell(rowTotalHeader, 5, "Tổng:", format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 6, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 7, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 8, this.promotionProductTotal.getQuantity(), format5);
            ExcelPoiUtils.createCell(rowTotalHeader, 9, this.promotionProductTotal.getPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalHeader, 10, this.promotionProductTotal.getTotalPrice(), format5);

            for (int i = 0; i < promotionIndays.size(); i++) {
                int column = 0;
                Row rowValue = sheet2.createRow(rowTable++);
                PromotionProductDTO record = promotionIndays.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, format);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDate(record.getOrderDate()), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCatName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getBarCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getUom(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getQuantity(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPrice(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getTotalPrice(), format4);
            }
            ExcelPoiUtils.autoSizeAllColumns(sheet2, 10);
            Row rowTotalFooter = sheet2.createRow(rowTable++);
            ExcelPoiUtils.createCell(rowTotalFooter, 5, "Tổng:", format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 6, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 7, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 8, this.promotionProductTotal.getQuantity(), format5);
            ExcelPoiUtils.createCell(rowTotalFooter, 9, this.promotionProductTotal.getPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalFooter, 10, this.promotionProductTotal.getTotalPrice(), format5);
        }

    }

    private void createTableSheet3() {
        int rowTable = 8;
        Row rowValues = sheet3.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowValues, 0, "STT", format1);
        ExcelPoiUtils.createCell(rowValues, 1, "NGÀNH HÀNG", format1);
        ExcelPoiUtils.createCell(rowValues, 2, "MÃ HÀNG", format1);
        ExcelPoiUtils.createCell(rowValues, 3, "BAR_CODE", format1);
        ExcelPoiUtils.createCell(rowValues, 4, "TÊN HÀNG", format1);
        ExcelPoiUtils.createCell(rowValues, 5, "TÊN IN HĐ", format1);
        ExcelPoiUtils.createCell(rowValues, 6, "ĐVT", format1);
        ExcelPoiUtils.createCell(rowValues, 7, "SL", format1);
        ExcelPoiUtils.createCell(rowValues, 8, "GIÁ", format1);
        ExcelPoiUtils.createCell(rowValues, 9, "THÀNH TIỀN", format1);

        if(!promotionproducts.isEmpty()) {
            Row rowTotalHeader = sheet3.createRow(rowTable++);
            ExcelPoiUtils.createCell(rowTotalHeader, 5, "Tổng:", format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 6, null, format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 7, this.promotionProductTotal.getQuantity(), format5);
            ExcelPoiUtils.createCell(rowTotalHeader, 8, this.promotionProductTotal.getPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalHeader, 9, this.promotionProductTotal.getTotalPrice(), format5);

            for (int i = 0; i < promotionproducts.size(); i++) {
                int column = 0;
                Row rowValue = sheet3.createRow(rowTable++);
                PromotionProductDTO record = promotionproducts.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCatName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getBarCode(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getProductName(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getUom(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getQuantity(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPrice(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getTotalPrice(), format4);
            }
            ExcelPoiUtils.autoSizeAllColumns(sheet3, 9);
            Row rowTotalFooter = sheet3.createRow(rowTable++);
            ExcelPoiUtils.createCell(rowTotalFooter, 5, "Tổng:", format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 6, null, format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 7, this.promotionProductTotal.getQuantity(), format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 8, this.promotionProductTotal.getPrice(), format5);
            ExcelPoiUtils.createCell(rowTotalFooter, 9, this.promotionProductTotal.getTotalPrice(), format5);
        }

    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        this.createTableSheet2();
        this.createTableSheet3();
        return this.getStream(workbook);
    }
}
