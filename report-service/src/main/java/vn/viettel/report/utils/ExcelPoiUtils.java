package vn.viettel.report.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class ExcelPoiUtils {

    public final static String TITLE_LEFT_BOLD = "title_left_bold";
    public final static String HEADER_LEFT_BOLD = "header_left_bold";
    public final static String HEADER_LEFT= "header_left";
    public final static String ITALIC_12 = "italic_12";
    public final static String BOLD_10 = "bold_10";
    public final static String BOLD_9 = "bold_9";
    public final static String BOLD_10_CL255_204_153 = "bold_10_cl255_204_153";
    public final static String DATA = "data";
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
        sheet.autoSizeColumn(colIndex);
    }
    public static Map<String, CellStyle> createStyles(XSSFWorkbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        DataFormat fmt = wb.createDataFormat();
        /**Init Font */
        XSSFFont headerFontBold = wb.createFont();
        setFontPOI(headerFontBold, "Time New Roman", 15, true,true, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont titleBold = wb.createFont();
        setFontPOI(titleBold, "Time New Roman", 15, true,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont headerFont = wb.createFont();
        setFontPOI(headerFont, "Time New Roman", 11, false,true, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont italic_12 = wb.createFont();
        setFontPOI(italic_12, "Time New Roman", 12, false,true, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont bold_10 = wb.createFont();
        setFontPOI(bold_10, "Time New Roman", 10, true,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont data = wb.createFont();
        setFontPOI(data, "Time New Roman", 9, false,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont bold_9 = wb.createFont();
        setFontPOI(bold_9, "Time New Roman", 9, true,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        /** Init cell style*/
        CellStyle styleHeader1 = wb.createCellStyle();
        styleHeader1.setFont(headerFontBold);
        byte[] rgb = new byte[]{(byte)192, (byte)192, (byte)192};
        XSSFCellStyle styleHeader = (XSSFCellStyle)styleHeader1;
        XSSFColor colorHeader = new XSSFColor(rgb,null);
        styleHeader.setFillForegroundColor(colorHeader);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader1,BorderStyle.THIN, poiBlackNew);
        /** Header style LEFT bold*/
        CellStyle styleHeader4 = wb.createCellStyle();
        styleHeader4.setFont(titleBold);
        styleHeader4.setAlignment(HorizontalAlignment.LEFT);
        styleHeader4.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(TITLE_LEFT_BOLD, styleHeader4);
        ////////////////////////////////////////////////////////////////////////
        /** Header style bold*/
        CellStyle styleHeader2 = wb.createCellStyle();
        styleHeader2.setFont(headerFontBold);
        styleHeader2.setAlignment(HorizontalAlignment.LEFT);
        styleHeader2.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(HEADER_LEFT_BOLD, styleHeader2);
        ////////////////////////////////////////////////////////////////////////
        /** Header style*/
        CellStyle styleHeader3 = wb.createCellStyle();
        styleHeader3.setFont(headerFont);
        styleHeader3.setAlignment(HorizontalAlignment.LEFT);
        styleHeader3.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(HEADER_LEFT, styleHeader3);
        ////////////////////////////////////////////////////////////////////////
        /**style 5*/
        CellStyle styleHeader5 = wb.createCellStyle();
        styleHeader5.setFont(italic_12);
        styleHeader5.setAlignment(HorizontalAlignment.LEFT);
        styleHeader5.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(ITALIC_12, styleHeader5);
        ////////////////////////////////////////////////////////////////////////
        /**bold_10_style_6,7*/
        CellStyle styleHeader6 = wb.createCellStyle();
        styleHeader6.setFont(bold_10);
        byte[] rgb2 = new byte[]{(byte)192, (byte)192, (byte)192};
        XSSFCellStyle styleHeader7 = (XSSFCellStyle)styleHeader6;
        XSSFColor colorHeader2 = new XSSFColor(rgb2,null);
        styleHeader7.setFillForegroundColor(colorHeader2);
        styleHeader7.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader7.setFont(bold_10);
        styleHeader7.setAlignment(HorizontalAlignment.LEFT);
        styleHeader7.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader6,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_10, styleHeader6);
        ////////////////////////////////////////////////////////////////////////
        /**bold_10_style_8,9*/
        CellStyle styleHeader8 = wb.createCellStyle();
        styleHeader8.setFont(bold_10);
        byte[] rgb3 = new byte[]{(byte)253, (byte)204, (byte)153};
        XSSFCellStyle styleHeader9 = (XSSFCellStyle)styleHeader8;
        XSSFColor colorHeader3 = new XSSFColor(rgb3,null);
        styleHeader9.setFillForegroundColor(colorHeader3);
        styleHeader9.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader9.setFont(bold_10);
        styleHeader9.setAlignment(HorizontalAlignment.LEFT);
        styleHeader9.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader8,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_10_CL255_204_153, styleHeader8);
        ////////////////////////////////////////////////////////////////////////
        /**data_style_10*/
        CellStyle styleHeader10 = wb.createCellStyle();
        styleHeader10.setFont(data);
        styleHeader10.setAlignment(HorizontalAlignment.LEFT);
        styleHeader10.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader10,BorderStyle.THIN, poiBlackNew);
        styles.put(DATA, styleHeader10);

        ////////////////////////////////////////////////////////////////////////
        /**bold_9*/
        CellStyle styleHeader11 = wb.createCellStyle();
        styleHeader11.setFont(bold_9);

        styleHeader11.setAlignment(HorizontalAlignment.LEFT);
        styleHeader11.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader11,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_9, styleHeader11);
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
