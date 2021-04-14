package vn.viettel.report.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class ExcelPoiUtils {

    public final static String TITLE_LEFT_BOLD = "title_left_bold";



    /**
     * Merged theo toa do, add value, format
     * @author nghianh1
     * @param sheet
     * @param colIndex
     * @param rowIndex
     * @param endColIndex
     * @param endRowIndex
     * @param value
     * @param cellFormat
     */
    public static void addCellsAndMerged(XSSFSheet sheet, int colIndex, int rowIndex, int endColIndex, int endRowIndex, Object value, CellStyle cellFormat) {
        sheet.autoSizeColumn(colIndex);
        for (int i = rowIndex; i <= endRowIndex; i++) {
            Row row1 = sheet.getRow(i) == null ? sheet.createRow(i) : sheet.getRow(i);
            for (int j = colIndex; j <= endColIndex; j++) {
                Cell cell1 = row1.getCell(j) == null ? row1.createCell(j) : row1.getCell(j);
                cell1.setCellStyle(cellFormat);
                if (i == rowIndex && j == colIndex) {
                    if (value != null) {
                        if (value instanceof BigDecimal) {
                            cell1.setCellValue(((BigDecimal) value).doubleValue());
                        } else if (value instanceof Integer) {
                            cell1.setCellValue(((Integer) value).doubleValue());
                        } else if (value instanceof Long) {
                            cell1.setCellValue(((Long) value).doubleValue());
                        } else if (value instanceof Float) {
                            cell1.setCellValue(((Float) value).doubleValue());
                        } else if (value instanceof String) {
                            cell1.setCellValue((String) value);
                        } else if (value instanceof Double) {
                            cell1.setCellValue(((Double) value).doubleValue());
                        }
                    }
                }
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, endRowIndex, colIndex, endColIndex));
    }
    /**
     * Dinh dang kieu format cho Cell cho Object
     *
     * @author nghianh1
     * @param sheet
     * @param colIndex
     * @param rowIndex
     * @param value
     * @param cellFormat
     */
    public static void addCell(XSSFSheet sheet, int colIndex, int rowIndex, Object value, CellStyle cellFormat){
        sheet.autoSizeColumn(colIndex);
        Row row = sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);
        Cell cell = row.getCell(colIndex) == null ? row.createCell(colIndex) : row.getCell(colIndex);
        if (value != null) {
            if (value instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) value).doubleValue());
            } else if (value instanceof Integer) {
                cell.setCellValue(((Integer) value).doubleValue());
            } else if (value instanceof Long) {
                cell.setCellValue(((Long) value).doubleValue());
            } else if (value instanceof Float) {
                cell.setCellValue(((Float) value).doubleValue());
            }else if (value instanceof Double) {
                cell.setCellValue(((Double) value).doubleValue());
            } else if (value instanceof String) {
                cell.setCellValue((String) value);
            }
        }
        if (cellFormat != null) {
            cell.setCellStyle(cellFormat);
        }
    }

    public static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        DataFormat fmt = (DataFormat) wb.createDataFormat();
        /**
         * Example set style
         *
         * @author nghianh1
         * @param sheet
         * @param colIndex
         * @param rowIndex
         * @param value
         * @param cellFormat
         */
        /////////////////////////////////////////////////////////////////////////
        CellStyle styleHeader1 = wb.createCellStyle();
        XSSFFont font = (XSSFFont) wb.createFont();
        font.setBold(true);
        font.setItalic(false);
        font.setFontHeight(10);
        font.setFontName("Times New Roman");
        styleHeader1.setFont(font);
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
        styles.put(TITLE_LEFT_BOLD, styleHeader1);
        ////////////////////////////////////////////////////////////////////////
        return styles;
    }

}
