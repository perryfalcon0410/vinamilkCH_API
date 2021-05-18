package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.utils.ExcelPoiUtils;
import vn.viettel.report.utils.NameHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomerNotTradeExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private ShopDTO shop;
    private Date fromDate;
    private Date toDate;

    private List<CustomerReportDTO> customers;

    public CustomerNotTradeExcel(List<CustomerReportDTO> customers, ShopDTO shop, Date fromDate, Date toDate) {
        this.customers = customers;
        this.shop = shop;
        workbook = new XSSFWorkbook();
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
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+3,row-2,colm+9,rowm-2,"CÔNG TY CỔ PHẦN SỮA VIỆT NAM",style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+3,row-1,colm+9,rowm-1,"Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+3,row,colm+9,rowm,"Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",style.get(ExcelPoiUtils.HEADER_LEFT));
        //
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO KHÁCH HÀNG KHÔNG GIAO DỊCH",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+ fromDate +"  ĐẾN NGÀY: "+ toDate, style.get(ExcelPoiUtils.ITALIC_12));
        //
        String[] headers = NameHeader.customerNotTradeHeader.split(";");

        if(null != headers && headers.length >0) {
            for (String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
            }
        }
    }

    private void writeDataLines() {
        int stt = 0, col, row = 9;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);

        for (CustomerReportDTO data : customers) {
            stt++;
            col = 0;
            row++;
            ExcelPoiUtils.addCell(sheet, col++, row, stt, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getCustomerCode(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getCustomerName(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getAddress(), style.get(ExcelPoiUtils.DATA));
        }
    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
