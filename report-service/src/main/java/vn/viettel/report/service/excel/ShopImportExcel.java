 package vn.viettel.report.service.excel;

 import org.apache.poi.ss.usermodel.CellStyle;
 import org.apache.poi.ss.usermodel.Row;
 import org.apache.poi.xssf.streaming.SXSSFSheet;
 import org.apache.poi.xssf.streaming.SXSSFWorkbook;
 import vn.viettel.core.dto.ShopDTO;
 import vn.viettel.core.messaging.CoverResponse;
 import vn.viettel.core.util.DateUtils;
 import vn.viettel.core.utils.ExcelPoiUtils;
 import vn.viettel.core.utils.NameHeader;
 import vn.viettel.report.messaging.ShopImportFilter;
 import vn.viettel.report.service.dto.ShopImportDTO;
 import vn.viettel.report.service.dto.ShopImportTotalDTO;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.text.ParseException;
 import java.time.format.DateTimeFormatter;
 import java.util.List;
 import java.util.Map;

 public class ShopImportExcel extends ExcelPoiUtils{
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data;
    private ShopDTO shop;
    private ShopDTO parentShop;
    private ShopImportFilter filter;
    private String[] headers;
    private String[] headers1;

    public ShopImportExcel(CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data, ShopDTO shop,ShopDTO parentShop, ShopImportFilter filter) {
        this.data = data;
        this.shop = shop;
        this.parentShop = parentShop;
        this.filter = filter;
        workbook = new SXSSFWorkbook();
    }

    private void writeHeaderLine() throws ParseException {
        Map<String, CellStyle> style = this.createStyles(workbook);
        int col = 0,col_=4,row =0;
        int colm = 9,rowm =0;
        sheet = workbook.createSheet("Sản phẩm");
        //header left
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName()==null?"":shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress()==null?"":(shop.getAddress()==null?"":shop.getAddress()) ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (shop.getPhone()!=null? shop.getPhone():"") + " Fax: " + (shop.getFax()!=null?shop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        if(parentShop != null) {
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: " + (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        }

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO NHẬP HÀNG CHI TIẾT",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+ DateUtils.formatDate2StringDate(filter.getFromDate())+"  ĐẾN NGÀY: "+DateUtils.formatDate2StringDate(filter.getToDate()),style.get(ExcelPoiUtils.ITALIC_12));
        //
        headers = NameHeader.header.split(";");
        if(null != headers && headers.length >0){
            for(String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
            }
            ExcelPoiUtils.autoSizeAllColumns(sheet, 24);
        }
    }
    private void writeDataLines() {
        int stt = 0,col,row = 9,col_=4, lastCol = 0;
        Map<String, CellStyle> style = this.createStyles(workbook);
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        CellStyle formatBold = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        CellStyle formatCurrencyBold = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY);

        Row rowTotalHeader = sheet.createRow(row);
        ExcelPoiUtils.createCell(rowTotalHeader, 0, null, format);
        ExcelPoiUtils.createCell(rowTotalHeader, 1, null, format);
        ExcelPoiUtils.createCell(rowTotalHeader, 2, null, format);
        ExcelPoiUtils.createCell(rowTotalHeader, 3, null, format);
        ExcelPoiUtils.createCell(rowTotalHeader, 4, "Tổng:", formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 5, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 6, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 7, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 8, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 9, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 10,  data.getInfo().getTotalQuantity(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 11, data.getInfo().getTotalWholeSale(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 12, data.getInfo().getTotalRetail(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 13, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 14, data.getInfo().getTotalAmount(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 15, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 16, data.getInfo().getTotal(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 17, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 18, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 19, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 20, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 21, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 22, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 23, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalHeader, 24, null, formatBold);

        for (ShopImportDTO s : data.getResponse()){
            stt++;col=0;row++;
            ExcelPoiUtils.addCell(sheet,col++,row,stt,format);
            String strDate = DateUtils.formatDate2StringDateTime(s.getTransDate());
            ExcelPoiUtils.addCell(sheet,col++,row,strDate,format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getImportType(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRedInvoiceNo(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getPoNumber(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getInternalNumber(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,DateUtils.formatDate2StringDate(s.getOrderDate()),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductInfoName(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductCode(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getProductName(),format);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getQuantity(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getWholesale(),formatCurrency);
            ExcelPoiUtils.addCell(sheet,col++,row,s.getRetail(),formatCurrency);
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
            if(col > lastCol) lastCol = col;
        }

        Row rowTotalFooter = sheet.createRow(row+1);
        ExcelPoiUtils.createCell(rowTotalFooter, 0, null, format);
        ExcelPoiUtils.createCell(rowTotalFooter, 1, null, format);
        ExcelPoiUtils.createCell(rowTotalFooter, 2, null, format);
        ExcelPoiUtils.createCell(rowTotalFooter, 3, null, format);
        ExcelPoiUtils.createCell(rowTotalFooter, 4, "Tổng:", formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 5, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 6, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 7, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 8, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 9, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 10,  data.getInfo().getTotalQuantity(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 11, data.getInfo().getTotalWholeSale(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 12, data.getInfo().getTotalRetail(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 13, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 14, data.getInfo().getTotalAmount(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 15, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 16, data.getInfo().getTotal(), formatCurrencyBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 17, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 18, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 19, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 20, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 21, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 22, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 23, null, formatBold);
        ExcelPoiUtils.createCell(rowTotalFooter, 24, null, formatBold);

        ExcelPoiUtils.autoSizeAllColumns(sheet, lastCol);
    }

    public ByteArrayInputStream export() throws IOException, ParseException {
        writeHeaderLine();
        writeDataLines();
        return this.getStream(workbook);
    }
}
