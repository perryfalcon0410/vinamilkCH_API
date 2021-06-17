package vn.viettel.report.service.excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExchangeTransExcel {
    private ShopDTO shopDTO;
    private ShopDTO parentShop;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    ExchangeTransFilter filter;
    ExchangeTransReportDTO tableDynamicDTO;
    Map<String, CellStyle> style;

    public ExchangeTransExcel(ExchangeTransFilter filter, ShopDTO shopDTO,ExchangeTransReportDTO tableDynamicDTO,ShopDTO parentShop) {
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

        List<Object[]> dataset = (List<Object[]>) tableDynamicDTO.getResponse();
        Double totalAmount = 0.0;
        for(int i = 0; i < dataset.size(); i++) {
            row++;
            Object[] datas =  dataset.get(i);
            ExcelPoiUtils.addCell(sheet,0, row, i + 1, style.get(ExcelPoiUtils.DATA));
            for(int j = 0; j < datas.length; j ++) {
                if(j == 0 && datas[j] != null) {
                    Date dateTime = (Date) datas[j];
                    ExcelPoiUtils.addCell(sheet,j+1, row, DateUtils.formatDate2StringDate(dateTime), style.get(ExcelPoiUtils.DATA_CURRENCY));
                }else {
                    ExcelPoiUtils.addCell(sheet,j+1, row, datas[j], style.get(ExcelPoiUtils.DATA_CURRENCY));
                }
            }
            Object[] lastData =  dataset.get(dataset.size()-1);
            ExcelPoiUtils.addCell(sheet,0, dataset.size()+8, "", style.get(ExcelPoiUtils.DATA));
            for(int j = 0; j < datas.length; j ++){
                    ExcelPoiUtils.addCell(sheet,j+1, dataset.size()+8,lastData[j], style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
                    BigDecimal temp = (BigDecimal) lastData[8];
                    totalAmount = temp.doubleValue();
            }
        }
        ExcelPoiUtils.addCell(sheet,1, dataset.size()+8, "Tổng cộng", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));

        ExcelPoiUtils.addCellsAndMerged(sheet,1,dataset.size()+11,2,dataset.size()+11,"Doanh số",style.get(ExcelPoiUtils.DATA_SMALL_TABLE));
        ExcelPoiUtils.addCellsAndMerged(sheet,1,dataset.size()+12,2,dataset.size()+12,"Đinh mức", style.get(ExcelPoiUtils.DATA_SMALL_TABLE));
        ExcelPoiUtils.addCellsAndMerged(sheet,1,dataset.size()+13,2,dataset.size()+13,"Số tiền đề nghị duyệt", style.get(ExcelPoiUtils.DATA_SMALL_TABLE));
        List<Object[]> dataset2 = (List<Object[]>) tableDynamicDTO.getExchangeRate();
        double quota = 0.0;
        for(int i = 0; i < dataset2.size(); i++) {
            Object[] datas =  dataset2.get(i);
            for(int j = 0; j < datas.length; j ++) {
                ExcelPoiUtils.addCell(sheet,3, dataset.size()+11+j,datas[j], style.get(ExcelPoiUtils.DATA_SMALL_TABLE));
                BigDecimal temp = (BigDecimal) datas[1];
                quota = temp.doubleValue();
            }
        }
        if (quota<=totalAmount) {
            ExcelPoiUtils.addCell(sheet,3, dataset.size()+13,quota,style.get(ExcelPoiUtils.DATA_SMALL_TABLE));
        }else ExcelPoiUtils.addCell(sheet,3, dataset.size()+13,totalAmount,style.get(ExcelPoiUtils.DATA_SMALL_TABLE));
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        if(tableDynamicDTO.getResponse() != null) this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
