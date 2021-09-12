package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class QuantitySalesReceiptExcel extends ExcelPoiUtils{
    private SXSSFWorkbook workbook = new SXSSFWorkbook();
    private Map<String, CellStyle> style = this.createStyles(workbook);
    private SXSSFSheet sheet;
    private QuantitySalesReceiptFilter filter;
    private TableDynamicDTO tableDynamicDTO;
    private ShopDTO shop;
    private ShopDTO parentShop;

    public QuantitySalesReceiptExcel(QuantitySalesReceiptFilter filter, TableDynamicDTO tableDynamicDTO, ShopDTO shop, ShopDTO parentShop ) {
        this.filter = filter;
        this.tableDynamicDTO = tableDynamicDTO;
        this.shop = shop;
        this.parentShop = parentShop;
    }

    private void writeHeaderLine()  {
        String fromDate = DateUtils.formatDate2StringDate(filter.getFromDate());
        String toDate = DateUtils.formatDate2StringDate(filter.getToDate());

        int col = 0, row =0, colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,(shop.getShopName()==null?"":shop.getShopName()),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,(shop.getAddress()==null?"":shop.getAddress()) ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (shop.getPhone()!=null? shop.getPhone():"") + " Fax: " + (shop.getFax()!=null?shop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        if(parentShop != null) {
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: " + (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        }

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO BÁN HÀNG THEO HÓA ĐƠN",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+fromDate+"  ĐẾN NGÀY: "+toDate,style.get(ExcelPoiUtils.ITALIC_12));

    }

    private void writeDataLines() {
        int row = 8;
        int col = 0, lastCol = 0;
        CellStyle formatBold = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        ExcelPoiUtils.addCell(sheet,col++, row, "STT", formatBold);
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ KHÁCH HÀNG", formatBold);
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN KHÁCH HÀNG", formatBold);
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ", formatBold);
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        List<String> dates = tableDynamicDTO.getDates();

        for(String date: dates) {
            ExcelPoiUtils.addCell(sheet,col++, row, date, formatBold);
        }
        ExcelPoiUtils.addCell(sheet,col++, row, "TỔNG CỘNG", formatBold);

        List<Object[]> dataset = (List<Object[]>) tableDynamicDTO.getResponse();

        for(int i = 0; i < dataset.size(); i++) {
            row++;
            Object[] datas =  dataset.get(i);
            ExcelPoiUtils.addCell(sheet,0, row, i + 1, format);
            for(int j = 0; j < datas.length ; j ++) {
                ExcelPoiUtils.addCell(sheet,j+1, row, datas[j], formatCurrency);
                if(j+1 > lastCol) lastCol = j+1;
            }
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet, lastCol);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        if(tableDynamicDTO.getResponse() != null) {
            this.writeDataLines();
        }
        return this.getStream(workbook);
    }
}
