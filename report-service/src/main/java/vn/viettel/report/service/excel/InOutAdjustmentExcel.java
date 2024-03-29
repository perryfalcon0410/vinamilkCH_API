package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.core.utils.NameHeader;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InOutAdjustmentExcel extends ExcelPoiUtils{
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private List<InOutAdjusmentDTO> data;
    private ShopDTO shop;
    private ShopDTO parentShop;
    private InOutAdjustmentFilter filter;
    private String[] headers;

    public InOutAdjustmentExcel(List<InOutAdjusmentDTO> data, ShopDTO shop,  ShopDTO parentShop, InOutAdjustmentFilter filter) {
        this.data = data;
        this.shop = shop;
        this.parentShop = parentShop;
        this.filter = filter;
        workbook = new SXSSFWorkbook();
    }
    private void writeHeaderLine()  {
        Map<String, CellStyle> style = this.createStyles(workbook);
        int col = 0,col_=4,row =0;
        int colm = 9,rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (shop.getPhone()!=null? shop.getPhone():"") + " Fax: " + (shop.getFax()!=null?shop.getFax():"") ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        if(parentShop != null) {
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: " + (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        }

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO NHẬP XUẤT ĐIỀU CHỈNH",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+ DateUtils.formatDate2StringDate(filter.getFromDate())+"  ĐẾN NGÀY: "+DateUtils.formatDate2StringDate(filter.getToDate()),style.get(ExcelPoiUtils.ITALIC_12));
        //
        headers = NameHeader.inOutAdjustmentHeader.split(";");
        if(null != headers && headers.length >0){
            for(String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
            }
            ExcelPoiUtils.autoSizeAllColumns(sheet, 12);
        }
    }
    private void writeDataLines() {
        int stt = 0,col,row = 8,lastCol=0;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        for (InOutAdjusmentDTO s : data){
            stt++;col=0;row++;
            ExcelPoiUtils.addCell(sheet,col++,row,stt,format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getShopCode(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRedInvoiceNo(),format);
            String strDate = DateUtils.formatDate2StringDate(s.getAdjustmentDate());
            ExcelPoiUtils.addCell(sheet,col++,row,strDate,format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTypess(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductInfoName(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductCode(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductName(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getUom1(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getQuantity(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPrice(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTotal(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getWarehouseTypeName(),format);
            if(col > lastCol) lastCol = col;
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet, lastCol);
    }
    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        return this.getStream(workbook);
    }
}
