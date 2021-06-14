package vn.viettel.sale.excel;


import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.utils.ExcelPoiUtils;
import vn.viettel.report.utils.NameHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SampleExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private ShopDTO shop;
    private ShopDTO shop_;
    private LocalDateTime date;
    String[] headers;
    public SampleExcel( ShopDTO shop,ShopDTO shop_, LocalDateTime date) {
        workbook = new XSSFWorkbook();
        this.shop = shop;
        this.shop_ = shop_;
        this.date = date;
    }
    private void writeHeaderLine() {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        int col = 0,col_=4,row =0,col__=0;
        int colm = 9,rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,"CÔNG TY CỔ PHẦN SỮA VIỆT NAM",style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,"Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO NHẬP XUẤT ĐIỀU CHỈNH",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String strDate = date.format(formatter);
        ExcelPoiUtils.addCell(sheet,col,row+4,"Ngày: "+strDate,style.get(ExcelPoiUtils.ITALIC_12));
        ExcelPoiUtils.addCell(sheet,col+3,row+5,"x: bắt buộc",style.get(ExcelPoiUtils.NOT_BOLD_11_RED));
        headers = NameHeader.sampleInventoryHeader.split(";");
        if(null != headers && headers.length >0){
            for(String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
                ExcelPoiUtils.addCell(sheet, col__++, row + 7,"", style.get(ExcelPoiUtils.DATA));
            }
        }

    }


    public  ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}