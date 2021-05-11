 package vn.viettel.report.excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import vn.viettel.report.utils.ExcelPoiUtils;
import vn.viettel.report.utils.NameHeader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ShopImportReport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data;
    private ShopDTO shop;
    private ShopImportFilter filter;
    String[] headers;
    String[] headers1;
    public ShopImportReport(CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data, ShopDTO shop,ShopImportFilter filter) {
        this.data = data;
        this.shop = shop;
        this.filter = filter;
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine()  {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        int col = 0,col_=4,row =0;
        int colm = 9,rowm =0;
        sheet = workbook.createSheet("Sản phẩm");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,"CÔNG TY CỔ PHẦN SỮA VIỆT NAM",style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,"Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",style.get(ExcelPoiUtils.HEADER_LEFT));
        //
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO NHẬP HÀNG CHI TIẾT",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+filter.getFromDate()+"  ĐẾN NGÀY: "+filter.getToDate(),style.get(ExcelPoiUtils.ITALIC_12));
        //
        headers = NameHeader.header.split(";");
        headers1 = NameHeader.header1.split(";");
        if(null != headers && headers.length >0){
            for(String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
                boolean result = Arrays.stream(headers1).anyMatch(h::equals);
                if (result) {
                    if (h.equals("SỐ PO")) {
                        ExcelPoiUtils.addCell(sheet, col_++, row + 7, "TỔNG :", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                    } else
                        ExcelPoiUtils.addCell(sheet, col_++, row + 7, "", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                }
            }
        }
    }
    private void writeDataLines() {
        int stt = 0,col,row = 9,col_=4;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        for (ShopImportDTO s : data.getResponse()){
            stt++;col=0;row++;
            ExcelPoiUtils.addCell(sheet,col++,row,stt,style.get(ExcelPoiUtils.DATA));
            DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
            String strDate = dateFormat.format(s.getTransDate());
            ExcelPoiUtils.addCell(sheet,col++,row,strDate,style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getImportType(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRedInvoiceNo(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPoNumber(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getInternalNumber(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getOrderDate(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductInfoName(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductCode(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductName(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getQuantity(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getWholesale(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRetail(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPriceNotVat(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getAmount(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPrice(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTotal(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getUom2(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getUom1(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTransCode(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getShopName(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTypeShop(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductGroup(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getNote(),style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet,col++,row,s.getReturnCode(),style.get(ExcelPoiUtils.DATA));
        }
        row= row+1;
        if(null != headers && headers.length >0){
            for(String h : headers) {
                boolean result = Arrays.stream(headers1).anyMatch(h::equals);
                if (result) {
                    if (h.equals("SỐ PO")) {
                        ExcelPoiUtils.addCell(sheet,col_++, row, "TỔNG :", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                    } else
                        ExcelPoiUtils.addCell(sheet,col_++, row, "", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                }
            }
        }
        for(String h : headers) {
            if(h.equals("SỐ LƯỢNG")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
            }else if(h.equals("SL PACKET")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalWholeSale(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalWholeSale(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
            }else if (h.equals("SL LẺ")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalRetail(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalRetail(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
            }else if(h.equals("THÀNH TIỀN")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
            }else if(h.equals("TỔNG CỘNG")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotal(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotal(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
            }
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
