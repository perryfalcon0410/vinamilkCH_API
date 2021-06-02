package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ImportExportInventoryExcel {

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet;
    PrintInventoryDTO inventoryDTO;
    ShopDTO parentShop;
    InventoryImportExportFilter filter;
    Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);

    public ImportExportInventoryExcel(ShopDTO parentShop, PrintInventoryDTO inventoryDTO, InventoryImportExportFilter filter) {
        this.parentShop = parentShop;
        this.inventoryDTO = inventoryDTO;
        this.filter = filter;
    }

    private void writeHeaderLine()  {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fromDate = null;
        String toDate = null;
        if(filter.getFromDate() != null) fromDate = dateFormat.format(filter.getFromDate());
        if(filter.getToDate() != null) toDate = dateFormat.format(filter.getToDate());

        int col = 0, row =0, colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,inventoryDTO.getShop().getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,inventoryDTO.getShop().getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+inventoryDTO.getShop().getPhone()+"  "+"Fax:"+" "+inventoryDTO.getShop().getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel:"+" "+parentShop.getPhone()+"  "+"Fax:"+" "+parentShop.getFax(),style.get(ExcelPoiUtils.HEADER_LEFT));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO XUẤT NHẬP TỒN",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+fromDate+"  ĐẾN NGÀY: "+toDate,style.get(ExcelPoiUtils.ITALIC_12));

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

        row=10;

        ImportExportInventoryTotalDTO total = inventoryDTO.getTotal();
        if(total!= null) {
            List<ImportExportInventoryDTO> products = inventoryDTO.getProducts();
            this.writeTotal(total, row++);
            for(int i = 0; i < products.size(); i++) {
                int index = i +1;
                ImportExportInventoryDTO product = products.get(i);

                ExcelPoiUtils.addCell(sheet, 0, row, index,  style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet, 1, row, product.getCatName(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet, 2, row, product.getProductCode(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet, 3, row, product.getProductName(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,4, row, product.getUom(), style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet, 5, row, product.getBeginningQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet, 6, row, product.getBeginningPrice(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                ExcelPoiUtils.addCell(sheet,7, row, product.getBeginningAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));

                ExcelPoiUtils.addCell(sheet, 8, row, product.getImpTotalQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,9, row, product.getImpQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,10, row, product.getImpAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                ExcelPoiUtils.addCell(sheet,11, row, product.getImpAdjustmentQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,12, row, product.getImpAdjustmentAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));

                ExcelPoiUtils.addCell(sheet,13, row, product.getExpTotalQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,14, row, product.getExpSalesQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,15, row, product.getExpSalesAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                ExcelPoiUtils.addCell(sheet,16, row, product.getExpPromotionQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,17, row, product.getExpPromotionAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                ExcelPoiUtils.addCell(sheet,18, row, product.getExpAdjustmentQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,19, row, product.getExpAdjustmentAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                ExcelPoiUtils.addCell(sheet,20, row, product.getExpExchangeQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,21, row, product.getExpExchangeAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));

                ExcelPoiUtils.addCell(sheet,22, row, product.getEndingQty(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,23, row, product.getEndingPrice(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                ExcelPoiUtils.addCell(sheet,24, row, product.getEndingAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));

                row++;
            }
            this.writeTotal(total, row);
        }

    }


    private void writeTotal( ImportExportInventoryTotalDTO total, int row) {
        int col = 5;
        ExcelPoiUtils.addCell(sheet, col++, row , total.getBeginningQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , null, style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getBeginningAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));

        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpTotalQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpAdjustmentQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpAdjustmentAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));

        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpTotalQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpSalesQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpSalesAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpPromotionQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpPromotionAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpAdjustmentQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpAdjustmentAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpExchangeQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpExchangeAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));

        ExcelPoiUtils.addCell(sheet, col++, row , total.getEndingQty(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , null, style.get(ExcelPoiUtils.BOLD_10_CL255_255_204));
        ExcelPoiUtils.addCell(sheet, col++, row , total.getEndingAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY));
    }


    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
