package vn.viettel.sale.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.sale.service.dto.PoDetailDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportExcel {
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private SXSSFSheet sheet2;
    private ShopDTO shops;
    private List<PoDetailDTO> poDetails;
    private List<PoDetailDTO> poDetails2;
    public ExportExcel(List<PoDetailDTO> poDetails, List<PoDetailDTO> poDetails2, ShopDTO shops) {
        this.poDetails = poDetails;
        this.poDetails2 = poDetails2;
        this.shops = shops;
        workbook = new SXSSFWorkbook();
    }
    private void writeHeaderLine()  {
        sheet = workbook.createSheet("Sản phẩm");
        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        Row row5 = sheet.createRow(5);
        Row rowValues = sheet.createRow(8);

        ////////////////////////////////////////////////////////////////////
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(true);
        font.setItalic(true);
        font.setFontHeight(15);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row.setRowStyle(style);
        row2.setRowStyle(style);
        //////////////////////////////////////////////////////////////////
        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = (XSSFFont) workbook.createFont();
        font1.setBold(false);
        font1.setItalic(true);
        font1.setFontHeight(11);
        font1.setFontName("Times New Roman");
        style1.setFont(font1);
        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row1.setRowStyle(style1);
        row2.setRowStyle(style1);
        //////////////////////////////////////////////////////////////////
        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = (XSSFFont) workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName("Times New Roman");
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row5.setRowStyle(style2);
        //////////////////////////////////////////////////////////////////
        CellStyle styleHeader1 = workbook.createCellStyle();
        XSSFFont fontheader = (XSSFFont) workbook.createFont();
        fontheader.setBold(true);
        fontheader.setItalic(false);
        fontheader.setFontHeight(10);
        fontheader.setFontName("Times New Roman");
        styleHeader1.setFont(fontheader);
        byte[] rgb = new byte[]{(byte)192, (byte)192, (byte)192};
        XSSFCellStyle styleHeader = (XSSFCellStyle)styleHeader1;
        XSSFColor colorHeader = new XSSFColor(rgb,null);
        styleHeader.setFillForegroundColor(colorHeader);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        //////////////////////////////////////////////////////////////////////////////
        int rowf = 0,rowt = 0;
        int colf=0,colt = 6;
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
        sheet.addMergedRegion(new CellRangeAddress(rowf,rowt,colf+7,colt+6));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:G6"));
        ExcelPoiUtils.createCell(row, rowf, poDetails.get(0).getShopName(), style);
        ExcelPoiUtils.createCell(row, 7, shops.getShopName(), style);
        ExcelPoiUtils.createCell(row1, 0, poDetails.get(0).getShopAddress(), style1);
        ExcelPoiUtils.createCell(row1, 7, shops.getAddress(), style1);
        ExcelPoiUtils.createCell(row2, 0, poDetails.get(0).getShopContact(), style1);
        ExcelPoiUtils.createCell(row2, 7, "Tel: "+ shops.getPhone()+" Fax: "+shops.getFax(), style1);
        ExcelPoiUtils.createCell(row5, 0, "DANH SÁCH DỮ LIỆU", style2);
        ////////////////////////////////////////////////////////////////////////////////////////////
        ExcelPoiUtils.createCell(rowValues, 0, "STT", styleHeader);
        ExcelPoiUtils.createCell(rowValues, 1, "SO NO", styleHeader);
        ExcelPoiUtils.createCell(rowValues, 2, "MÃ SẢN PHẨM", styleHeader);
        ExcelPoiUtils.createCell(rowValues, 3, "TÊN SẢN PHẨM", styleHeader);
        ExcelPoiUtils.createCell(rowValues, 4, "GIÁ", styleHeader);
        ExcelPoiUtils.createCell(rowValues, 5, "SỐ LƯỢNG", styleHeader);
        ExcelPoiUtils.createCell(rowValues, 6, "THÀNH TIỀN", styleHeader);
        //////////////////////////////////////////////////////////////////////////////
        // SHEET 2
        ////////////////////////////////////////////////////////////////////////////
        if(poDetails2.size()>0)
        {
            sheet2 = workbook.createSheet("Hàng khuyến mãi");
            Row row_ = sheet2.createRow(0);
            Row row_1 = sheet2.createRow(1);
            Row row_2 = sheet2.createRow(2);
            Row row_5 = sheet2.createRow(5);
            Row row_Values = sheet2.createRow(8);
            int rowf2 = 0,rowt2 = 0;
            int colf2=0,colt2 = 6;
            sheet2.addMergedRegion(CellRangeAddress.valueOf("A1:G1"));
            sheet2.addMergedRegion(new CellRangeAddress(rowf,rowt2,colf2+7,colt2+6));
            sheet2.addMergedRegion(CellRangeAddress.valueOf("A2:G2"));
            sheet2.addMergedRegion(CellRangeAddress.valueOf("H2:M2"));
            sheet2.addMergedRegion(CellRangeAddress.valueOf("A3:G3"));
            sheet2.addMergedRegion(CellRangeAddress.valueOf("H3:M3"));
            sheet2.addMergedRegion(CellRangeAddress.valueOf("A6:G6"));
            ExcelPoiUtils.createCell(row_, 0, poDetails2.get(0).getShopName(), style);
            ExcelPoiUtils.createCell(row_, 7, shops.getShopName(), style);
            ExcelPoiUtils.createCell(row_1, 0, poDetails2.get(0).getShopAddress(), style1);
            ExcelPoiUtils.createCell(row_1, 7, shops.getAddress(), style1);
            ExcelPoiUtils.createCell(row_2, 0, poDetails2.get(0).getShopContact(), style1);
            ExcelPoiUtils.createCell(row_2, 7, "Tel: "+shops.getPhone()+ " Fax: "+shops.getFax(), style1);
            ExcelPoiUtils.createCell(row_5, 0, "DANH SÁCH DỮ LIỆU", style2);
            ////////////////////////////////////////////////////////////////////////////////////////////
            ExcelPoiUtils.createCell(row_Values, 0, "STT", styleHeader);
            ExcelPoiUtils.createCell(row_Values, 1, "SO NO", styleHeader);
            ExcelPoiUtils.createCell(row_Values, 2, "MÃ SẢN PHẨM", styleHeader);
            ExcelPoiUtils.createCell(row_Values, 3, "TÊN SẢN PHẨM", styleHeader);
            ExcelPoiUtils.createCell(row_Values, 4, "GIÁ", styleHeader);
            ExcelPoiUtils.createCell(row_Values, 5, "SỐ LƯỢNG", styleHeader);
            ExcelPoiUtils.createCell(row_Values, 6, "THÀNH TIỀN", styleHeader);
        }
    }

    private void writeDataLines() {
        int rowCount = 9;
        int rowCount_ = 9;

        //////////////////////////////////////////////////////////////////
        CellStyle styleValues = workbook.createCellStyle();
        XSSFFont fontValues = (XSSFFont) workbook.createFont();
        fontValues.setBold(false);
        fontValues.setItalic(false);
        fontValues.setFontHeight(9);
        fontValues.setFontName("Times New Roman");
        styleValues.setFont(fontValues);
        styleValues.setBorderTop(BorderStyle.THIN);
        styleValues.setBorderBottom(BorderStyle.THIN);
        styleValues.setBorderLeft(BorderStyle.THIN);
        styleValues.setBorderRight(BorderStyle.THIN);
        //////////////////////////////////////////////////
        CellStyle styleCurrency = workbook.createCellStyle();
        XSSFFont fontCurrency = (XSSFFont) workbook.createFont();
        DataFormat dataFormat22 = workbook.createDataFormat();
        fontCurrency.setBold(false);
        fontCurrency.setItalic(false);
        fontCurrency.setFontHeight(9);
        fontCurrency.setFontName("Times New Roman");
        styleCurrency.setFont(fontCurrency);
        styleCurrency.setDataFormat(dataFormat22.getFormat("#,###"));
        styleCurrency.setBorderTop(BorderStyle.THIN);
        styleCurrency.setBorderBottom(BorderStyle.THIN);
        styleCurrency.setBorderLeft(BorderStyle.THIN);
        styleCurrency.setBorderRight(BorderStyle.THIN);
        //////////////////////////////////////////////////////////////////
        int stt = 1;
        for (PoDetailDTO poDetail : poDetails) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            ExcelPoiUtils.createCell(row, columnCount++, stt++, styleValues);
            ExcelPoiUtils.createCell(row, columnCount++, poDetail.getSoNo(), styleValues);
            ExcelPoiUtils.createCell(row, columnCount++, poDetail.getProductCode(), styleValues);
            ExcelPoiUtils.createCell(row, columnCount++, poDetail.getProductName(), styleValues);
            ExcelPoiUtils.createCell(row, columnCount++, poDetail.getPriceNotVat(), styleCurrency);
            ExcelPoiUtils.createCell(row, columnCount++, poDetail.getQuantity(), styleCurrency);
            ExcelPoiUtils.createCell(row, columnCount++, poDetail.getAmountNotVat(), styleCurrency);
        }
        if(poDetails2.size()>0){
            int stt_ = 1;
            for (PoDetailDTO poDetail2 : poDetails2) {
                Row row =sheet2.createRow(rowCount_++);
                int columnCount = 0;
                ExcelPoiUtils.createCell(row, columnCount++, stt_++, styleValues);
                ExcelPoiUtils.createCell(row, columnCount++, poDetail2.getSoNo(), styleValues);
                ExcelPoiUtils.createCell(row, columnCount++, poDetail2.getProductCode(), styleValues);
                ExcelPoiUtils.createCell(row, columnCount++, poDetail2.getProductName(), styleValues);
                ExcelPoiUtils.createCell(row, columnCount++, poDetail2.getPriceNotVat(), styleValues);
                ExcelPoiUtils.createCell(row, columnCount++, poDetail2.getQuantity(), styleCurrency);
                ExcelPoiUtils.createCell(row, columnCount++, poDetail2.getAmountNotVat(), styleValues);
            }
        }
    }
    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();
        ExcelPoiUtils.autoSizeAllColumns(workbook);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());

    }
}
