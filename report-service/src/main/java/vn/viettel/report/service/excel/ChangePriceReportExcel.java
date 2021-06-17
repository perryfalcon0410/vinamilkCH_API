package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.core.utils.NameHeader;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChangePriceReportExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
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
        workbook = new XSSFWorkbook();
        this.fromDate = fromDate;
        this.toDate = toDate;

        for (ChangePriceDTO changePrice : changePriceReport.getChangePriceReport()) {
            if (!listParent.stream().anyMatch(e -> e.getPoNumber().equals(changePrice.getPoNumber())))
                listParent.add(new ChangePriceTotalDTO(changePrice.getPoNumber()));
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
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,"CÔNG TY CỔ PHẦN SỮA VIỆT NAM",style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,"Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",style.get(ExcelPoiUtils.HEADER_LEFT));
        //
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO CHÊNH LỆCH GIÁ",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+ fromDate +"  ĐẾN NGÀY: "+ toDate, style.get(ExcelPoiUtils.ITALIC_12));
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
                    }else {
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

        ExcelPoiUtils.addCell(sheet,4,9, changePriceReport.getReportTotal().getTotalQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));

        for (int i = 0; i < listParent.size(); i ++) {
            ExcelPoiUtils.addCellsAndMerged(sheet,1,rowMerge,3,rowMerge,listParent.get(i).getPoNumber(),style.get(ExcelPoiUtils.BOLD_9));
            ExcelPoiUtils.addCell(sheet,4,rowMerge,listParent.get(i).getTotalQuantity(),style.get(ExcelPoiUtils.BOLD_9));
            for (ChangePriceDTO data : listChildByParent.get(i)) {
                row = rowMerge;
                stt++;col=0;row++;rowMerge++;
                ExcelPoiUtils.addCell(sheet,col++,row,stt,style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getProductCode(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getProductName(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getUnit(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getQuantity(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getInputPrice(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getTotalInput(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getOutputPrice(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getTotalOutput(),style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,col++,row,data.getPriceChange(),style.get(ExcelPoiUtils.DATA));
            }
            rowMerge = row + 1;
        }
        ExcelPoiUtils.addCell(sheet,4,row + 1, changePriceReport.getReportTotal().getTotalQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
    }
    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
