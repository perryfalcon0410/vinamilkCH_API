package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.core.utils.NameHeader;
import vn.viettel.report.service.dto.CustomerReportDTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomerNotTradeExcel {
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private ShopDTO shop;
    private ShopDTO parentShop;
    private Date fromDate;
    private Date toDate;

    private List<CustomerReportDTO> customers;

    public CustomerNotTradeExcel(List<CustomerReportDTO> customers, ShopDTO shop,  ShopDTO parentShop, Date fromDate, Date toDate) {
        this.customers = customers;
        this.shop = shop;
        this.parentShop = parentShop;
        workbook = new SXSSFWorkbook();
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    private void writeHeaderLine() {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        int col = 0,row =0;
        int colm = 2,rowm =0;
        sheet = workbook.createSheet("Khách Không Giao Dịch");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (shop.getMobiPhone()!=null? shop.getMobiPhone():"") + " Fax: " + (shop.getFax()!=null?shop.getFax():"") ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        if(parentShop !=null) {
            ExcelPoiUtils.addCellsAndMerged(sheet,col+3,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+3,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+3,row,colm+9,rowm,"Tel: " +  (parentShop.getMobiPhone()!=null?parentShop.getMobiPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        }

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO KHÁCH HÀNG KHÔNG GIAO DỊCH",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+ DateUtils.formatDate2StringDate(fromDate) +"  ĐẾN NGÀY: "+ DateUtils.formatDate2StringDate(toDate), style.get(ExcelPoiUtils.ITALIC_12));
        //
        String[] headers = NameHeader.customerNotTradeHeader.split(";");

        if(null != headers && headers.length >0) {
            for (String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
            }
            ExcelPoiUtils.autoSizeAllColumns(sheet, col);
        }
    }

    private void writeDataLines() {
        int stt = 0, col = 0, row = 9;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        CellStyle format = style.get(ExcelPoiUtils.DATA);

        for (CustomerReportDTO data : customers) {
            stt++;
            col = 0;
            ExcelPoiUtils.addCell(sheet, col++, row, stt, format);
            ExcelPoiUtils.addCell(sheet, col++, row, data.getCustomerCode(), format);
            ExcelPoiUtils.addCell(sheet, col++, row, data.getCustomerName(), format);
            /*ExcelPoiUtils.addCell(sheet, col++, row, data.getPhone(), format);
            ExcelPoiUtils.addCell(sheet, col++, row, DateUtils.formatDate2StringDate(data.getBirthDay()), format);
            ExcelPoiUtils.addCell(sheet, col++, row, data.getGender(), format);*/
            ExcelPoiUtils.addCell(sheet, col++, row, data.getAddress(), format);
            row++;
        }

        ExcelPoiUtils.autoSizeAllColumns(sheet, col);
    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        IOUtils.closeQuietly(out);
        return response;
    }
}
