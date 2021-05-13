package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Map;

public class ImportExportInventoryExcel {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private ShopDTO shop;
    Map<String, CellStyle> style;

    public ImportExportInventoryExcel(ShopDTO shopDTO) {
        this.shop = shopDTO;
        workbook = new XSSFWorkbook();
        style = ExcelPoiUtils.createStyles(workbook);
    }

    private void writeHeaderLine()  {
        int col = 0,col_=4,row =0;
        int colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,"CÔNG TY CỔ PHẦN SỮA VIỆT NAM",style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,"Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",style.get(ExcelPoiUtils.HEADER_LEFT));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO XUẤT NHẬP TỒN",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+"filter.getFromDate()"+"  ĐẾN NGÀY: "+"filter.getToDate()",style.get(ExcelPoiUtils.ITALIC_12));

    }

    private void writeDataLines() {
        int row = 8;

        ExcelPoiUtils.addCellsAndMerged(sheet,0, row, 0 , row + 1, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCellsAndMerged(sheet,1, row, 1 , row + 1, "NGÀNH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCellsAndMerged(sheet,2, row, 2 , row + 1, "MÃ HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCellsAndMerged(sheet,3, row, 3 , row + 1, "TÊN HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCellsAndMerged(sheet,4, row, 4 , row + 1, "ĐVT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCellsAndMerged(sheet,5, row, 7 , row, "ĐẦU KỲ", style.get(ExcelPoiUtils.BOLD_10_CL255_255_153));
        ExcelPoiUtils.addCell(sheet,5, row + 1, "SL", style.get(ExcelPoiUtils.BOLD_9_CL255_255_153));
        ExcelPoiUtils.addCell(sheet,6, row + 1, "Giá", style.get(ExcelPoiUtils.BOLD_9_CL255_255_153));
        ExcelPoiUtils.addCell(sheet,7, row + 1, "Thành tiền", style.get(ExcelPoiUtils.BOLD_9_CL255_255_153));

        ExcelPoiUtils.addCellsAndMerged(sheet,8, row, 12 , row, "NHẬP TRONG KỲ", style.get(ExcelPoiUtils.BOLD_10_CL255_204_0));
        ExcelPoiUtils.addCell(sheet,8, row + 1, "Tổng SL", style.get(ExcelPoiUtils.BOLD_9_CL255_204_0));
        ExcelPoiUtils.addCell(sheet,9, row + 1, "Nhập hàng", style.get(ExcelPoiUtils.BOLD_9_CL255_204_0));
        ExcelPoiUtils.addCell(sheet,10, row + 1, "Tiền nhập hàng", style.get(ExcelPoiUtils.BOLD_9_CL255_204_0));
        ExcelPoiUtils.addCell(sheet,11, row + 1, "SL điều chỉnh", style.get(ExcelPoiUtils.BOLD_9_CL255_204_0));
        ExcelPoiUtils.addCell(sheet,12, row + 1, "Tiền điều chỉnh", style.get(ExcelPoiUtils.BOLD_9_CL255_204_0));

        ExcelPoiUtils.addCellsAndMerged(sheet,13, row, 21 , row, "XUẤT TRONG KỲ", style.get(ExcelPoiUtils.BOLD_10_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,13, row + 1, "Tổng SL", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,14, row + 1, "Số lượng bán", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,15, row + 1, "Tiền bán hàng", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,16, row + 1, "KM (bán hàng)", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,17, row + 1, "Tiền KM", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,18, row + 1, "SL điều chỉnh", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,19, row + 1, "Tiền điều chỉnh", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,20, row + 1, "SL đổi hàng", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,21, row + 1, "Tiền đổi hàng", style.get(ExcelPoiUtils.BOLD_9_CL51_204_204));

        ExcelPoiUtils.addCellsAndMerged(sheet,22, row, 24 , row, "CUỐI KỲ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,22, row + 1, "SL", style.get(ExcelPoiUtils.BOLD_9_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,23, row + 1, "Giá", style.get(ExcelPoiUtils.BOLD_9_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,24, row + 1, "Thành tiền", style.get(ExcelPoiUtils.BOLD_9_CL192_192_192));

    }


    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
