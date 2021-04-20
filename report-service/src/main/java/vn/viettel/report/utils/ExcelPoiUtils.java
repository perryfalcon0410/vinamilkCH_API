package vn.viettel.report.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class ExcelPoiUtils {

    public final static String TITLE_LEFT_BOLD = "title_left_bold";

    /** Init Font color*/
    public final static XSSFColor poiBlackNew =  new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null);//Mau den
    /**
     * Merged base on coordinate , add value, format
     * @author nghianh1
     * @param sheet
     * @param colIndex col start
     * @param rowIndex row start
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
     *  Format cho Cell cho Object
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
    public static Map<String, CellStyle> createStyles(XSSFWorkbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        DataFormat fmt = wb.createDataFormat();

        /**Init Font */
        XSSFFont exampleFont = wb.createFont();
        setFontPOI(exampleFont, "Arial", 10, true,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));

        /** Init cell style*/
        CellStyle styleHeader1 = wb.createCellStyle();
        styleHeader1.setFont(exampleFont);
        byte[] rgb = new byte[]{(byte)192, (byte)192, (byte)192};
        XSSFCellStyle styleHeader = (XSSFCellStyle)styleHeader1;
        XSSFColor colorHeader = new XSSFColor(rgb,null);
        styleHeader.setFillForegroundColor(colorHeader);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader1,BorderStyle.THIN, poiBlackNew);
        styles.put(TITLE_LEFT_BOLD, styleHeader1);
        ////////////////////////////////////////////////////////////////////////
        return styles;
    }
    public static XSSFFont setFontPOI(XSSFFont fontStyle, String fontName, Integer fontHeight, Boolean isBold,Boolean isItalic, XSSFColor fontColor) {
        String fName = fontName == null ? "Arial" : fontName;
        Integer fHeight = fontHeight == null ? 9 : fontHeight;
        Boolean fIsBold = isBold == null ? false : isBold;
        Boolean fIsItalic = isItalic == null ? false : isItalic;
        /*byte[] rgb = new byte[]{(byte)0, (byte)0, (byte)0};*/
        XSSFColor fColor = fontColor == null ? new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null) :fontColor;
        fontStyle.setBold(fIsBold);
        fontStyle.setItalic(fIsItalic);
        fontStyle.setFontName(fName);
        fontStyle.setFontHeight(fHeight);
        fontStyle.setColor(fColor);
        return fontStyle;
    }
    /**
     * Set Border Style for a CellStyle
     * @author nghianh1
     * @param cellStyle
     * @param borderStyle
     * @param borderColor
     * @param params
     * @return
     */
    public static CellStyle setBorderForCell(CellStyle cellStyle, BorderStyle borderStyle, XSSFColor borderColor, BorderStyle... params) {
        Short border = borderStyle == null ? -1 : Short.valueOf(String.valueOf(borderStyle.ordinal()));
        Short color = borderColor == null ? -1 : borderColor.getIndexed();
        if (border != -1) {
            cellStyle.setBorderLeft(BorderStyle.valueOf(border));
            cellStyle.setBorderTop(BorderStyle.valueOf(border));
            cellStyle.setBorderRight(BorderStyle.valueOf(border));
            cellStyle.setBorderBottom(BorderStyle.valueOf(border));
        }
        //Gan mau cho border
        if (color != -1) {
            cellStyle.setTopBorderColor(color);
            cellStyle.setBottomBorderColor(color);
            cellStyle.setLeftBorderColor(color);
            cellStyle.setRightBorderColor(color);
        }
        if (params.length <= 4) {
            int i = 0;
            for (BorderStyle sideBorder : params) {
                Short sBoder = sideBorder == null ? -1 : Short.valueOf(String.valueOf(sideBorder.ordinal()));
                switch (i++) {
                    case (0):
                        if (sBoder != -1) {
                            cellStyle.setBorderTop(BorderStyle.valueOf(sBoder));
                        }
                        break;
                    case (1):
                        if (sBoder != -1) {
                            cellStyle.setBorderLeft(BorderStyle.valueOf(sBoder));
                        }
                        break;
                    case (2):
                        if (sBoder != -1) {
                            cellStyle.setBorderBottom(BorderStyle.valueOf(sBoder));
                        }
                        break;
                    case (3):
                        if (sBoder != -1) {
                            cellStyle.setBorderRight(BorderStyle.valueOf(sBoder));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return cellStyle;
    }
}
