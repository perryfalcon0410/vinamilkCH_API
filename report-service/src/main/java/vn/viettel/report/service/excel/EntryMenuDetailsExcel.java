package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.EntryMenuDetailsReportsRequest;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntryMenuDetailsExcel extends ExcelPoiUtils {

    private SXSSFWorkbook workbook = new SXSSFWorkbook();
    private SXSSFSheet sheet1;

    private ShopDTO shop;
    private ShopDTO parentShop;
    private List<EntryMenuDetailsDTO> entryMenuDetailsDTOS;
    private EntryMenuDetailsDTO entryMenuDetailsDTO;
    private EntryMenuDetailsReportsRequest filter;
    private Map<String, CellStyle> style = this.createStyles(workbook);;
    private CellStyle format = style.get(ExcelPoiUtils.DATA);
    private CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
    private CellStyle format2 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
    private CellStyle format3 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY);
    private CellStyle format4 = style.get(ExcelPoiUtils.DATA_CURRENCY);

    public EntryMenuDetailsExcel(ShopDTO shopDTO, ShopDTO parentShop, List<EntryMenuDetailsDTO> entryMenuDetailsDTOS, EntryMenuDetailsDTO total, EntryMenuDetailsReportsRequest filter) {
        this.shop = shopDTO;
        this.parentShop = parentShop;
        this.entryMenuDetailsDTOS = entryMenuDetailsDTOS;
        this.entryMenuDetailsDTO = total;
        this.filter = filter;
    }

    private void writeHeaderLine()  {
        List<SXSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("sheet1");
        sheets.add(sheet1);

        for(SXSSFSheet sheet: sheets) {
            int col = 0,row =0, colm = 9, rowm =0;

            ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (shop.getPhone()!=null? shop.getPhone():"") + " Fax: " + (shop.getFax()!=null?shop.getFax():"") ,style.get(ExcelPoiUtils.HEADER_LEFT));
            //header right
            if(parentShop != null) {
                ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
                ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
                ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: " +  (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));

            }

            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BẢNG KÊ CHI TIẾT CÁC HÓA ĐƠN NHẬP HÀNG ",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "
                    + DateUtils.formatDate2StringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(filter.getToDate()),style.get(ExcelPoiUtils.ITALIC_12));

        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        ExcelPoiUtils.createCell(rowHeader, 0, "STT", format1);
        ExcelPoiUtils.createCell(rowHeader, 1, "SOPO", format1);
        ExcelPoiUtils.createCell(rowHeader, 2, "SONOIBO", format1);
        ExcelPoiUtils.createCell(rowHeader, 3, "SOHD", format1);
        ExcelPoiUtils.createCell(rowHeader, 4, "NGAYHD", format1);
        ExcelPoiUtils.createCell(rowHeader, 5, "NGAYTT", format1);
        ExcelPoiUtils.createCell(rowHeader, 6, "SOTIEN", format1);
        ExcelPoiUtils.createCell(rowHeader, 7, "HDKM", format1);
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 7);

        if(!entryMenuDetailsDTOS.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);

            ExcelPoiUtils.createCell(rowTotalHeader, 4, "Tổng:" ,format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 5, null , format2);
            ExcelPoiUtils.createCell(rowTotalHeader, 6, this.entryMenuDetailsDTO.getTotalAmount() , format3);
            ExcelPoiUtils.createCell(rowTotalHeader, 7, null, format2);

            for (int i = 0; i < entryMenuDetailsDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                EntryMenuDetailsDTO record = entryMenuDetailsDTOS.get(i);

                ExcelPoiUtils.createCell(rowValue, column++, i + 1, format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPoNumber(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getInternalNumber(), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getRedInvoiceNo(), format);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDate(record.getBillDate()), format);
                ExcelPoiUtils.createCell(rowValue, column++, DateUtils.formatDate2StringDate(record.getDateOfPayment()), format);
                ExcelPoiUtils.createCell(rowValue, column++, record.getAmount(), format4);
                ExcelPoiUtils.createCell(rowValue, column++, record.getPromotionalOrders(),  format);
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);

            ExcelPoiUtils.createCell(rowTotalFooter, 4, "Tổng:", format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 5, null , format2);
            ExcelPoiUtils.createCell(rowTotalFooter, 6, this.entryMenuDetailsDTO.getTotalAmount() , format3);
            ExcelPoiUtils.createCell(rowTotalFooter, 7, null, format2);

        }
        ExcelPoiUtils.autoSizeAllColumns(sheet1, 7);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.createTableSheet1();
        return this.getStream(workbook);
    }
}
