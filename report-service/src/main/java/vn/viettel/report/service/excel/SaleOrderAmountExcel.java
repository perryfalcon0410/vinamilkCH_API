package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class SaleOrderAmountExcel {
    private XSSFWorkbook workbook = new XSSFWorkbook();
    Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
    private XSSFSheet sheet;
    SaleOrderAmountFilter filter;
    TableDynamicDTO tableDynamicDTO;
    ShopDTO shop;
    ShopDTO parentShop;

    public SaleOrderAmountExcel(SaleOrderAmountFilter filter, TableDynamicDTO tableDynamicDTO, ShopDTO shop, ShopDTO parentShop ) {
        this.filter = filter;
        this.tableDynamicDTO = tableDynamicDTO;
        this.shop = shop;
        this.parentShop = parentShop;
    }

    private void writeHeaderLine()  {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fromDate = dateFormat.format(filter.getFromDate());
        String toDate = dateFormat.format(filter.getToDate());
        int col = 0, row =0, colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel:"+" "+parentShop.getPhone()+"  "+"Fax:"+" "+parentShop.getFax(),style.get(ExcelPoiUtils.HEADER_LEFT));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO DOANH SỐ THEO HÓA ĐƠN",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+fromDate+"  ĐẾN NGÀY: "+toDate,style.get(ExcelPoiUtils.ITALIC_12));

    }

    private void writeDataLines() {
        int row = 8;
        int col = 0;
        ExcelPoiUtils.addCell(sheet,col++, row, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ KHÁCH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN KHÁCH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        List<String> dates = tableDynamicDTO.getDates();

        for(String date: dates) {
            ExcelPoiUtils.addCell(sheet,col++, row, date, style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        }
        ExcelPoiUtils.addCell(sheet,col++, row, "TỔNG CỘNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        List<Object[]> dataset = (List<Object[]>) tableDynamicDTO.getResponse();

        for(int i = 0; i < dataset.size(); i++) {
            row++;
            Object[] datas =  dataset.get(i);
            ExcelPoiUtils.addCell(sheet,0, row, i + 1, style.get(ExcelPoiUtils.DATA));
            for(int j = 0; j < datas.length ; j ++) {
                ExcelPoiUtils.addCell(sheet,j+1, row, datas[j], style.get(ExcelPoiUtils.DATA_CURRENCY));
            }
        }
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        if(tableDynamicDTO.getResponse() != null) this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
