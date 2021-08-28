package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ImportExportInventoryExcel {

    private SXSSFWorkbook workbook = new SXSSFWorkbook();
    private SXSSFSheet sheet;
    private PrintInventoryDTO inventoryDTO;
    private ShopDTO parentShop;
    private InventoryImportExportFilter filter;
    private Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
    private CellStyle format = style.get(ExcelPoiUtils.DATA);
    private CellStyle formatBold = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
    private CellStyle formatBold1 = style.get(ExcelPoiUtils.BOLD_10_CL255_255_153);
    private CellStyle formatBold2 = style.get(ExcelPoiUtils.BOLD_9_CL255_255_153);
    private CellStyle formatBold3 = style.get(ExcelPoiUtils.BOLD_9_CL255_204_0);
    private CellStyle formatBold4 = style.get(ExcelPoiUtils.BOLD_9_CL51_204_204);
    private CellStyle formatBold5 = style.get(ExcelPoiUtils.BOLD_9_CL192_192_192);
    private CellStyle formatBold6 = style.get(ExcelPoiUtils.BOLD_10_CL255_255_204);
    private CellStyle formatBold7 = style.get(ExcelPoiUtils.BOLD_10_CL255_255_204_FORMAT_CURRENCY);
    private CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
    private CellStyle formatBold10Center = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192_CENTER);

    public ImportExportInventoryExcel(ShopDTO parentShop, PrintInventoryDTO inventoryDTO, InventoryImportExportFilter filter) {
        this.parentShop = parentShop;
        this.inventoryDTO = inventoryDTO;
        this.filter = filter;
    }

    private void writeHeaderLine()  {
        String fromDate = DateUtils.formatDate2StringDate(filter.getFromDate());
        String toDate = DateUtils.formatDate2StringDate(filter.getToDate());

        int col = 0, row =0, colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,inventoryDTO.getShop().getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,inventoryDTO.getShop().getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (inventoryDTO.getShop().getMobiPhone()!=null? inventoryDTO.getShop().getMobiPhone():"") + " Fax: " + (inventoryDTO.getShop().getFax()!=null?inventoryDTO.getShop().getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        if(parentShop != null) {
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm, "Tel: " + (parentShop.getMobiPhone()!=null?parentShop.getMobiPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        }

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO XUẤT NHẬP TỒN",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+fromDate+"  ĐẾN NGÀY: "+toDate,style.get(ExcelPoiUtils.ITALIC_12));

    }

    private void writeDataLines() {
        int row = 8;

        ExcelPoiUtils.addCellsAndMerged(sheet,0, row, 0 , row + 1, "STT", formatBold10Center);
        ExcelPoiUtils.addCellsAndMerged(sheet,1, row, 1 , row + 1, "NGÀNH HÀNG", formatBold10Center);
        ExcelPoiUtils.addCellsAndMerged(sheet,2, row, 2 , row + 1, "MÃ HÀNG", formatBold10Center);
        ExcelPoiUtils.addCellsAndMerged(sheet,3, row, 3 , row + 1, "TÊN HÀNG", formatBold10Center);
        ExcelPoiUtils.addCellsAndMerged(sheet,4, row, 4 , row + 1, "ĐVT", formatBold10Center);

        ExcelPoiUtils.addCellsAndMerged(sheet,5, row, 7 , row, "ĐẦU KỲ", formatBold1);
        ExcelPoiUtils.addCell(sheet,5, row + 1, "SL", formatBold2);
        ExcelPoiUtils.addCell(sheet,6, row + 1, "Giá", formatBold2);
        ExcelPoiUtils.addCell(sheet,7, row + 1, "Thành tiền", formatBold2);

        ExcelPoiUtils.addCellsAndMerged(sheet,8, row, 12 , row, "NHẬP TRONG KỲ", style.get(ExcelPoiUtils.BOLD_10_CL255_204_0));
        ExcelPoiUtils.addCell(sheet,8, row + 1, "Tổng SL", formatBold3);
        ExcelPoiUtils.addCell(sheet,9, row + 1, "Nhập hàng", formatBold3);
        ExcelPoiUtils.addCell(sheet,10, row + 1, "Tiền nhập hàng", formatBold3);
        ExcelPoiUtils.addCell(sheet,11, row + 1, "SL điều chỉnh", formatBold3);
        ExcelPoiUtils.addCell(sheet,12, row + 1, "Tiền điều chỉnh", formatBold3);

        ExcelPoiUtils.addCellsAndMerged(sheet,13, row, 21 , row, "XUẤT TRONG KỲ", style.get(ExcelPoiUtils.BOLD_10_CL51_204_204));
        ExcelPoiUtils.addCell(sheet,13, row + 1, "Tổng SL", formatBold4);
        ExcelPoiUtils.addCell(sheet,14, row + 1, "Số lượng bán", formatBold4);
        ExcelPoiUtils.addCell(sheet,15, row + 1, "Tiền bán hàng", formatBold4);
        ExcelPoiUtils.addCell(sheet,16, row + 1, "KM (bán hàng)", formatBold4);
        ExcelPoiUtils.addCell(sheet,17, row + 1, "Tiền KM", formatBold4);
        ExcelPoiUtils.addCell(sheet,18, row + 1, "SL điều chỉnh", formatBold4);
        ExcelPoiUtils.addCell(sheet,19, row + 1, "Tiền điều chỉnh", formatBold4);
        ExcelPoiUtils.addCell(sheet,20, row + 1, "SL đổi hàng", formatBold4);
        ExcelPoiUtils.addCell(sheet,21, row + 1, "Tiền đổi hàng", formatBold4);

        ExcelPoiUtils.addCellsAndMerged(sheet,22, row, 24 , row, "CUỐI KỲ", formatBold10Center);
        ExcelPoiUtils.addCell(sheet,22, row + 1, "SL", formatBold5);
        ExcelPoiUtils.addCell(sheet,23, row + 1, "Giá", formatBold5);
        ExcelPoiUtils.addCell(sheet,24, row + 1, "Thành tiền", formatBold5);
        ExcelPoiUtils.autoSizeAllColumns(sheet, 24);
        row=10;

        ImportExportInventoryTotalDTO total = inventoryDTO.getTotal();
        if(total!= null) {
            List<ImportExportInventoryDTO> products = inventoryDTO.getProducts();
            this.writeTotal(total, row++);
            for(int i = 0; i < products.size(); i++) {
                int index = i +1;
                ImportExportInventoryDTO product = products.get(i);

                ExcelPoiUtils.addCell(sheet, 0, row, index,  format);
                ExcelPoiUtils.addCell(sheet, 1, row, product.getCatName(), format);
                ExcelPoiUtils.addCell(sheet, 2, row, product.getProductCode(), format);
                ExcelPoiUtils.addCell(sheet, 3, row, product.getProductName(), format);
                ExcelPoiUtils.addCell(sheet,4, row, product.getUom(), format);

                ExcelPoiUtils.addCell(sheet, 5, row, product.getBeginningQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet, 6, row, product.getBeginningPrice(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,7, row, product.getBeginningAmount(), formatCurrency);

                ExcelPoiUtils.addCell(sheet, 8, row, product.getImpTotalQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,9, row, product.getImpQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,10, row, product.getImpAmount(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,11, row, product.getImpAdjustmentQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,12, row, product.getImpAdjustmentAmount(), formatCurrency);

                ExcelPoiUtils.addCell(sheet,13, row, product.getExpTotalQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,14, row, product.getExpSalesQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,15, row, product.getExpSalesAmount(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,16, row, product.getExpPromotionQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,17, row, product.getExpPromotionAmount(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,18, row, product.getExpAdjustmentQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,19, row, product.getExpAdjustmentAmount(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,20, row, product.getExpExchangeQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,21, row, product.getExpExchangeAmount(), formatCurrency);

                ExcelPoiUtils.addCell(sheet,22, row, product.getEndingQty(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,23, row, product.getEndingPrice(), formatCurrency);
                ExcelPoiUtils.addCell(sheet,24, row, product.getEndingAmount(), formatCurrency);

                row++;
            }
            this.writeTotal(total, row);
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet, 24);
    }


    private void writeTotal( ImportExportInventoryTotalDTO total, int row) {
        int col = 0;
        ExcelPoiUtils.addCell(sheet, col++, row , null, format);
        ExcelPoiUtils.addCell(sheet, col++, row , null, format);
        ExcelPoiUtils.addCell(sheet, col++, row , null, format);
        ExcelPoiUtils.addCell(sheet, col++, row , null, format);
        ExcelPoiUtils.addCell(sheet, col++, row , null, format);

        ExcelPoiUtils.addCell(sheet, col++, row , total.getBeginningQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , null, formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getBeginningAmount(), formatBold7);

        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpTotalQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpAmount(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpAdjustmentQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getImpAdjustmentAmount(), formatBold7);

        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpTotalQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpSalesQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpSalesAmount(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpPromotionQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpPromotionAmount(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpAdjustmentQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpAdjustmentAmount(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpExchangeQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getExpExchangeAmount(), formatBold7);

        ExcelPoiUtils.addCell(sheet, col++, row , total.getEndingQty(), formatBold7);
        ExcelPoiUtils.addCell(sheet, col++, row , null, formatBold6);
        ExcelPoiUtils.addCell(sheet, col++, row , total.getEndingAmount(), formatBold7);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        IOUtils.closeQuietly(out);
        return response;
    }

}
