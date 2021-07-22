package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.core.utils.NameHeader;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.dto.PromotionProductDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ChangePriceReportExcel {
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private ChangePriceReportRequest changePriceReport;
    private ShopDTO shop;
    private LocalDate fromDate;
    private LocalDate toDate;

    private int rowNum = 1;

    private List<List<ChangePriceDTO>> listChildByParent = new ArrayList<>();
    private List<ChangePriceTotalDTO> listParent = new ArrayList<>();

    public ChangePriceReportExcel(ChangePriceReportRequest changePriceReport, ShopDTO shop, LocalDate fromDate, LocalDate toDate) {
        this.changePriceReport = changePriceReport;
        this.shop = shop;
        workbook = new SXSSFWorkbook();
        this.fromDate = fromDate;
        this.toDate = toDate;
        List<ChangePriceDTO> listData = changePriceReport.getChangePriceReport();
        Collections.sort(listData, Comparator.comparing(ChangePriceDTO::getOrderDate));
        List<LocalDateTime> listTime = listData.stream().map(item->item.getOrderDate().truncatedTo(ChronoUnit.DAYS)).distinct().collect(Collectors.toList());
        for (int i = 0;i<listTime.size();i++){
            LocalDateTime time = listTime.get(i);
            Long stt = 1L;
            for (ChangePriceDTO changePrice : listData) {
                LocalDateTime timeCompare = changePrice.getOrderDate().truncatedTo(ChronoUnit.DAYS);
                if(timeCompare.equals(time)){
                    if (!listParent.stream().anyMatch(e -> e.getPoNumber().equals(changePrice.getPoNumber()))){
                        listParent.add(new ChangePriceTotalDTO(changePrice.getRedInvoiceNo(), stt, changePrice.getOrderDate(), changePrice.getPoNumber(), changePrice.getInternalNumber(), changePrice.getTransCode()));
                        stt++;
                    }
                }
            }
        }
        for (ChangePriceTotalDTO poNum : listParent) {
            long totalQuantity = 0;
            List<ChangePriceDTO> subParent = new ArrayList<>();
            for (ChangePriceDTO changePrice : changePriceReport.getChangePriceReport()) {
                if (changePrice.getPoNumber().equals(poNum.getPoNumber())) {
                    subParent.add(changePrice);
                    totalQuantity += changePrice.getQuantity();
                    rowNum ++;
                }
            }
            poNum.setTotalQuantity(totalQuantity);
            listChildByParent.add(subParent);
        }
    }
    private void writeHeaderLine()  {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        int col = 0,col_=0,row =0,colTotal= 3;
        int colm = 9,rowm =0;
        sheet = workbook.createSheet("Chênh lệch giá");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+(shop.getPhone()==null?"":shop.getPhone())+"  "+"Fax:"+" "+(shop.getFax()==null?"":shop.getFax()) ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,"CÔNG TY CỔ PHẦN SỮA VIỆT NAM",style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,"Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",style.get(ExcelPoiUtils.HEADER_LEFT));
        //
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO CHÊNH LỆCH GIÁ",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+ DateUtils.formatDate2StringDate(fromDate) +"  ĐẾN NGÀY: "+ DateUtils.formatDate2StringDate(toDate), style.get(ExcelPoiUtils.ITALIC_12));

        //
        String[] headers = NameHeader.changePriceHeader.split(";");
        String[] headers1 = NameHeader.changePriceHeader1.split(";");

        if(null != headers && headers.length >0){
            for(String h : headers){
                ExcelPoiUtils.addCell(sheet,col++,row+6,h,style.get(ExcelPoiUtils.BOLD_10));
                boolean result = Arrays.stream(headers1).anyMatch(h::equals);
                if(!result){
                    ExcelPoiUtils.addCell(sheet,col_++,row+7,"",style.get(ExcelPoiUtils.HEADER_LEFT));
                }else{
                    if(h.equals("ĐVT")){
                        ExcelPoiUtils.addCell(sheet,col_++,row+7,"TỔNG CỘNG :",style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                        ExcelPoiUtils.addCell(sheet,colTotal++,row+7+listParent.size()+rowNum,"TỔNG CỘNG :",style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                    } else {
                        ExcelPoiUtils.addCell(sheet, col_++, row + 7, "", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                        ExcelPoiUtils.addCell(sheet, colTotal++, row+7+listParent.size()+rowNum, "", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                    }
                }
            }
        }
    }
    private void writeDataLines() {
        int stt = 0,col,row = 0;
        int rowMerge = 10;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        CellStyle format = style.get(ExcelPoiUtils.DATA_CURRENCY);
        CellStyle format1 = style.get(ExcelPoiUtils.BOLD_9);
        CellStyle format2 = style.get(ExcelPoiUtils.BOLD_9_LEFT);
        CellStyle format3 = style.get(ExcelPoiUtils.BORDER_RIGHT);
        ExcelPoiUtils.addCell(sheet,4,9, changePriceReport.getReportTotal().getTotalQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,6,9, changePriceReport.getReportTotal().getTotalPriceInput() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,8,9, changePriceReport.getReportTotal().getTotalPriceOutput() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        int lastCol = 0;
        for (int i = 0; i < listParent.size(); i ++) {
            ExcelPoiUtils.addCellsAndMerged(sheet,1,rowMerge,3,rowMerge,"Số HĐ: "+(listParent.get(i).getRedInvoiceNo()==null?"":listParent.get(i).getRedInvoiceNo()+"-")
                    +(listParent.get(i).getStt()==null?"":listParent.get(i).getStt()+"-")+
                   DateUtils.formatDate2StringDate(listParent.get(i).getOrderDate())+"-"+
                    (listParent.get(i).getPoNumber()==null?"":listParent.get(i).getPoNumber()+"-")+
                    (listParent.get(i).getInternalNumber()==null?"":listParent.get(i).getInternalNumber()+"-")+
                    (listParent.get(i).getTransCode()==null?"":listParent.get(i).getTransCode()),format2);
            ExcelPoiUtils.addCell(sheet,4,rowMerge,listParent.get(i).getTotalQuantity(),format1);
            ExcelPoiUtils.addCellsAndMerged(sheet,5,rowMerge,9,rowMerge,"",format2);
            for (ChangePriceDTO data : listChildByParent.get(i)) {
                row = rowMerge;
                stt++;col=0;row++;rowMerge++;
                ExcelPoiUtils.addCell(sheet,col++,row,stt,format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getProductCode(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getProductName(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getUnit(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getQuantity(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getInputPrice(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getTotalInput(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getOutputPrice(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getTotalOutput(),format);
                ExcelPoiUtils.addCell(sheet,col++,row,data.getPriceChange(),format);
                if(col > lastCol) lastCol = col;
            }
            rowMerge = row + 1;
        }
        ExcelPoiUtils.addCell(sheet,4,row + 1, changePriceReport.getReportTotal().getTotalQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,6,row + 1, changePriceReport.getReportTotal().getTotalPriceInput() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,8,row + 1, changePriceReport.getReportTotal().getTotalPriceOutput() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.autoSizeAllColumns(sheet, lastCol);
    }
    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
