package vn.viettel.report.service.excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExchangeTransExcel {
    private ShopDTO shopDTO;
    private ShopDTO parentShop;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    ExchangeTransFilter filter;
    TableDynamicDTO tableDynamicDTO;
    Map<String, CellStyle> style;

    public ExchangeTransExcel(ExchangeTransFilter filter, ShopDTO shopDTO,TableDynamicDTO tableDynamicDTO,ShopDTO parentShop) {
        workbook = new XSSFWorkbook();
        {
            this.filter = filter;
            this.shopDTO = shopDTO;
            this.tableDynamicDTO = tableDynamicDTO;
            this.parentShop = parentShop;
            this.style = ExcelPoiUtils.createStyles(workbook);
        }
    }

    private void writeHeaderLine()  {
        String fromDate = DateUtils.formatDate2StringDate(filter.getFromDate());
        String toDate = DateUtils.formatDate2StringDate(filter.getToDate());

        int col = 0, row =0, colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shopDTO.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shopDTO.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shopDTO.getPhone()+"  "+"Fax:"+" "+shopDTO.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
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
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY BIÊN BẢN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ BIÊN BẢN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ KHÁCH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN KHÁCH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ SẢN PHẨM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN SẢN PHẨM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ LƯỢNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "THÀNH TIỀN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "LÍ DO", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ ĐT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        List<String> dates = tableDynamicDTO.getDates();

        for(String date: dates) {
            ExcelPoiUtils.addCell(sheet,col++, row, date, style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        }
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
