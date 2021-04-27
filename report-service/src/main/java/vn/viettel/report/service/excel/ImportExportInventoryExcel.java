package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImportExportInventoryExcel {
    private static final String FONT_NAME= "Times New Roman";

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private ShopDTO shopDTO;

    public ImportExportInventoryExcel(ShopDTO shopDTO) {
        this.shopDTO = shopDTO;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine()  {

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setItalic(true);
        font.setFontHeight(15);
        font.setFontName(FONT_NAME);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = workbook.createFont();
        font1.setBold(false);
        font1.setItalic(true);
        font1.setFontHeight(11);
        font1.setFontName(FONT_NAME);
        style1.setFont(font1);
        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setItalic(false);
        font2.setFontHeight(15);
        font2.setFontName(FONT_NAME);
        style2.setFont(font2);
        style2.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        sheet = workbook.createSheet("KM_ChiTiet");

        Row row = sheet.createRow(0);
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        Row row5 = sheet.createRow(5);
        Row row7 = sheet.createRow(7);

        row.setRowStyle(style);
        row1.setRowStyle(style1);
        row2.setRowStyle(style1);
        row5.setRowStyle(style2);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J1:Q1"));
        createCell(sheet, row, 0, shopDTO.getShopName(), style);
        createCell(sheet, row, 9, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A2:I2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J2:Q2"));
        createCell(sheet, row1, 0, shopDTO.getAddress(), style1);
        createCell(sheet, row1, 9, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style1);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("J3:Q3"));
        createCell(sheet, row2, 0,"Tel: " + shopDTO.getMobiPhone() + " Fax: " + shopDTO.getFax(), style1);
        createCell(sheet, row2, 9, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style1);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A6:Y6"));
        createCell(sheet, row5, 0, "BÁO CÁO XUẤT NHẬP TỒN", style2);

        sheet.addMergedRegion(CellRangeAddress.valueOf("A8:Y8"));
        createCell(sheet, row7, 0, "TỪ NGÀY: 01/04/2021   ĐẾN NGÀY: 23/04/2021", style1);
    }

    private void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        }
        else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
//        this.createTableSheet();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
