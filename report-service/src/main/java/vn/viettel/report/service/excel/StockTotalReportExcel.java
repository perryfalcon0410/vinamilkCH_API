package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.core.utils.NameHeader;
import vn.viettel.report.service.dto.StockTotalExcelRequest;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

public class StockTotalReportExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private StockTotalExcelRequest stockTotalExcelRequest;
    private ShopDTO shop;
    private LocalDate toDate;

    private int rowNum = 1;

    public StockTotalReportExcel(StockTotalExcelRequest stockTotalExcelRequest, ShopDTO shop, LocalDate toDate) {
        this.stockTotalExcelRequest = stockTotalExcelRequest;
        this.shop = shop;
        workbook = new XSSFWorkbook();
        this.toDate = toDate;
    }

    private void writeHeaderLine() {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        int col = 0, col_ = 0, row = 0, colTotal = 3;
        int colm = 9, rowm = 0;
        sheet = workbook.createSheet("Tồn Kho Cửa Hàng");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet, col, row, colm, rowm, shop.getShopName(), style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, shop.getAddress(), style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, "Tel:" + " " + shop.getPhone() + "  " + "Fax:" + " " + shop.getFax(), style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 2, colm + 9, rowm - 2, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 1, colm + 9, rowm - 1, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row, colm + 9, rowm, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style.get(ExcelPoiUtils.HEADER_LEFT));
        //
        ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 3, colm + 15, rowm + 3, "BÁO CÁO TỒN KHO CỬA HÀNG", style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 5, colm + 15, rowm + 5, "ĐẾN NGÀY: " + toDate, style.get(ExcelPoiUtils.ITALIC_12));
        //
        String[] headers = NameHeader.stockTotalHeader.split(";");
        String[] headers1 = NameHeader.stockTotalHeader1.split(";");

        if (null != headers && headers.length > 0) {
            for (String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row + 6, h, style.get(ExcelPoiUtils.BOLD_10));
                boolean result = Arrays.stream(headers1).anyMatch(h::equals);
                if (!result) {
                    ExcelPoiUtils.addCell(sheet, col_++, row + 7, "", style.get(ExcelPoiUtils.HEADER_LEFT));
                } else {
                    if (h.equals("TÊN SẢN PHẨM")) {
                        ExcelPoiUtils.addCell(sheet, col_++, row + 7, "TỔNG: ", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                        ExcelPoiUtils.addCell(sheet, colTotal++, row + 7 + stockTotalExcelRequest.getStockTotals().size() + rowNum, "TỔNG: ", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                    } else {
                        ExcelPoiUtils.addCell(sheet, col_++, row + 7, "", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                        ExcelPoiUtils.addCell(sheet, colTotal++, row + 7 + stockTotalExcelRequest.getStockTotals().size() + rowNum, "", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
                    }
                }
            }
        }
    }

    private void writeDataLines() {
        int stt = 0,col,row = 9,col_=4;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);

        ExcelPoiUtils.addCell(sheet,4,row, stockTotalExcelRequest.getTotalInfo().getTotalQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,5,row, stockTotalExcelRequest.getTotalInfo().getTotalPackageQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,6,row, stockTotalExcelRequest.getTotalInfo().getTotalUnitQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,8,row, stockTotalExcelRequest.getTotalInfo().getTotalAmount() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));

        for (StockTotalReportDTO data : stockTotalExcelRequest.getStockTotals()) {
            stt++;col=0;row++;

            ExcelPoiUtils.addCell(sheet, col++, row, stt, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getProductCategory(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getProductCode(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getProductName(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getStockQuantity(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getPacketQuantity(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getUnitQuantity(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getPrice(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getTotalAmount(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getPacketUnit(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getUnit(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getShop(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getShopType(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getProductGroup(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getMinInventory(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getMaxInventory(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet, col++, row, data.getWarning(), style.get(ExcelPoiUtils.DATA));
        }
        ExcelPoiUtils.addCell(sheet,4,row + 1, stockTotalExcelRequest.getTotalInfo().getTotalQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,5,row + 1, stockTotalExcelRequest.getTotalInfo().getTotalPackageQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,6,row + 1, stockTotalExcelRequest.getTotalInfo().getTotalUnitQuantity() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
        ExcelPoiUtils.addCell(sheet,8,row + 1, stockTotalExcelRequest.getTotalInfo().getTotalAmount() ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153));
    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
