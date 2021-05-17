package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;
import vn.viettel.report.utils.ExcelPoiUtils;
import vn.viettel.report.utils.NameHeader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class InOutAdjustmentExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<InOutAdjusmentDTO> data;
    private ShopDTO shop;
    private InOutAdjustmentFilter filter;
    String[] headers;
    public InOutAdjustmentExcel(List<InOutAdjusmentDTO> data, ShopDTO shop, InOutAdjustmentFilter filter) {
        this.data = data;
        this.shop = shop;
        this.filter = filter;
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine()  {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        int col = 0,col_=4,row =0;
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
        //
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO NHẬP XUẤT ĐIỀU CHỈNH",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+filter.getFromDate()+"  ĐẾN NGÀY: "+filter.getToDate(),style.get(ExcelPoiUtils.ITALIC_12));
        //
        headers = NameHeader.inOutAdjustmentHeader.split(";");
        if(null != headers && headers.length >0){
            for(String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
            }
        }
    }
    private void writeDataLines() {
        int stt = 0,col,row = 8,col_=4;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        for (InOutAdjusmentDTO s : data){
            stt++;col=0;row++;
            ExcelPoiUtils.addCell(sheet,col++,row,stt,style.get(ExcelPoiUtils.DATA));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            String strDate = dateFormat.format(s.getAdjustmentDate());
            ExcelPoiUtils.addCell(sheet,col++,row,s.getShopCode(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRedInvoiceNo(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,strDate,style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTypess(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductInfoName(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductCode(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductName(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getUom1(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getQuantity(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPrice(),style.get(ExcelPoiUtils.DATA_CURRENCY));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTotal(),style.get(ExcelPoiUtils.DATA_CURRENCY));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getWarehouseTypeName(),style.get(ExcelPoiUtils.DATA));
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
