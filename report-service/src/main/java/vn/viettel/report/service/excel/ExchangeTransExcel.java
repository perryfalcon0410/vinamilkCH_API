package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    ExchangeTransFilter filter;
    ExchangeTransReportDTO tableDynamicDTO;
    Map<String, CellStyle> style;

    public ExchangeTransExcel(ExchangeTransFilter filter, ShopDTO shopDTO,ExchangeTransReportDTO tableDynamicDTO,ShopDTO parentShop) {
        workbook = new SXSSFWorkbook();
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
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,6,rowm,shopDTO.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,6,++rowm, shopDTO.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,6,++rowm,"Tel: " + (shopDTO.getMobiPhone()!=null? shopDTO.getMobiPhone():"") + " Fax: " + (shopDTO.getFax()!=null?shopDTO.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        if(parentShop != null){
            ExcelPoiUtils.addCellsAndMerged(sheet,7,row-2,12,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,7,row-1,12,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,7,row,12,rowm, "Tel: " +  (parentShop.getMobiPhone()!=null?parentShop.getMobiPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        }

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+12,rowm+3,"BẢNG TỔNG HỢP ĐỔI HÀNG HƯ HỎNG",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+12,rowm+5,"TỪ NGÀY: "+fromDate+"  ĐẾN NGÀY: "+toDate,style.get(ExcelPoiUtils.ITALIC_12));
        ExcelPoiUtils.autoSizeAllColumns(sheet, col+10);

    }

    private void writeDataLines() {
        int row = 8;
        int col = 0;
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
        CellStyle format2 = style.get(ExcelPoiUtils.DATA_SMALL_TABLE);
        CellStyle format3 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
        ExcelPoiUtils.addCell(sheet,col++, row, "STT", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY BIÊN BẢN", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ BIÊN BẢN", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ KHÁCH HÀNG", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN KHÁCH HÀNG", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ SẢN PHẨM", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN SẢN PHẨM", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ LƯỢNG", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "THÀNH TIỀN", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "LÍ DO", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ ĐT", format1);
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        List<Object[]> dataset = (List<Object[]>) tableDynamicDTO.getResponse();
        Double totalAmount = 0.0;
        for(int i = 0; i < dataset.size(); i++) {
            row++;
            Object[] datas =  dataset.get(i);
            ExcelPoiUtils.addCell(sheet,0, row, i + 1, format);
            for(int j = 0; j < datas.length; j ++) {
                if(j == 0 && datas[j] != null) {
                    Date dateTime = (Date) datas[j];
                    ExcelPoiUtils.addCell(sheet,j+1, row, DateUtils.formatDate2StringDate(dateTime), formatCurrency);
                }else {
                    ExcelPoiUtils.addCell(sheet,j+1, row, datas[j], formatCurrency);
                }
            }
            Object[] lastData =  tableDynamicDTO.getTotals();
            ExcelPoiUtils.addCell(sheet,0, dataset.size()+9, "", format);
            for(int j = 0; j < datas.length; j ++){
                    ExcelPoiUtils.addCell(sheet,j+1, dataset.size()+9,lastData[j], format3);
                    BigDecimal temp = (BigDecimal) lastData[8];
                    totalAmount = temp.doubleValue();
            }
        }
        ExcelPoiUtils.addCell(sheet,1, dataset.size()+9, "Tổng cộng", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));

        ExcelPoiUtils.addCellsAndMerged(sheet,1,dataset.size()+11,2,dataset.size()+11,"Doanh số",format2);
        ExcelPoiUtils.addCellsAndMerged(sheet,1,dataset.size()+12,2,dataset.size()+12,"Đinh mức", format2);
        ExcelPoiUtils.addCellsAndMerged(sheet,1,dataset.size()+13,2,dataset.size()+13,"Số tiền đề nghị duyệt", format2);
        List<Object[]> dataset2 = (List<Object[]>) tableDynamicDTO.getExchangeRate();
        double quota = 0.0;
        for(int i = 0; i < dataset2.size(); i++) {
            Object[] datas =  dataset2.get(i);
            for(int j = 0; j < datas.length; j ++) {
                ExcelPoiUtils.addCell(sheet,3, dataset.size()+11+j,datas[j], format2);
                BigDecimal temp = (BigDecimal) datas[1];
                quota = temp!=null?temp.doubleValue():0.0;
            }
        }
        if (quota<=totalAmount) {
            ExcelPoiUtils.addCell(sheet,3, dataset.size()+13,quota,format2);
        }else ExcelPoiUtils.addCell(sheet,3, dataset.size()+13,totalAmount,format2);
        ExcelPoiUtils.autoSizeAllColumns(sheet, col);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        if(tableDynamicDTO.getResponse() != null) {
            this.writeDataLines();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        IOUtils.closeQuietly(out);
        return response;
    }
}
