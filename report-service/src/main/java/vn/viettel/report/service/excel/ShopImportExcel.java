 package vn.viettel.report.service.excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.core.utils.NameHeader;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ShopImportExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data;
    private ShopDTO shop;
    private ShopDTO shop_;
    private ShopImportFilter filter;
    String[] headers;
    String[] headers1;
    public ShopImportExcel(CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data, ShopDTO shop,ShopDTO shop_, ShopImportFilter filter) {
        this.data = data;
        this.shop = shop;
        this.shop_ = shop_;
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
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,shop_.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,shop_.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel:"+" "+shop_.getPhone()+"  "+"Fax:"+" "+shop_.getFax(),style.get(ExcelPoiUtils.HEADER_LEFT));
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
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        CellStyle formatBold = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);

        for (ShopImportDTO s : data.getResponse()){
            stt++;col=0;row++;
            ExcelPoiUtils.addCell(sheet,col++,row,stt,format);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String strDate = s.getTransDate().format(formatter);
            ExcelPoiUtils.addCell(sheet,col++,row,strDate,format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getImportType(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRedInvoiceNo(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPoNumber(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getInternalNumber(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getOrderDate(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductInfoName(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductCode(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductName(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getQuantity(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getWholesale(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRetail(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPriceNotVat(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getAmount(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPrice(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTotal(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getUom2(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getUom1(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTransCode(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getShopName(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getTypeShop(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductGroup(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getNote(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getReturnCode(),format);
        }
        row= row+1;
        if(null != headers && headers.length >0){
            for(String h : headers) {
                boolean result = Arrays.stream(headers1).anyMatch(h::equals);
                if (result) {
                    if (h.equals("SỐ PO")) {
                        ExcelPoiUtils.addCell(sheet,col_++, row, "TỔNG :", formatBold);
                    } else
                        ExcelPoiUtils.addCell(sheet,col_++, row, "", formatBold);
                }
            }
        }
        for(String h : headers) {
            if(h.equals("SỐ LƯỢNG")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalQuantity(), formatBold);
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalQuantity(), formatBold);
            }else if(h.equals("SL PACKET")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalWholeSale(), formatBold);
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalWholeSale(), formatBold);
            }else if (h.equals("SL LẺ")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalRetail(), formatBold);
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalRetail(), formatBold);
            }else if(h.equals("THÀNH TIỀN")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotalAmount(), formatBold);
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotalAmount(), formatBold);
            }else if(h.equals("TỔNG CỘNG")){
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), row, data.getInfo().getTotal(), formatBold);
                ExcelPoiUtils.addCell(sheet,Arrays.asList(headers).indexOf(h), 9, data.getInfo().getTotal(), formatBold);
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
