package vn.viettel.core.utils;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public final class ExcelPoiUtils {

    public final static String TITLE_LEFT_BOLD = "title_left_bold";
    public final static String HEADER_LEFT_BOLD = "header_left_bold";
    public final static String HEADER_LEFT= "header_left";
    public final static String ITALIC_12 = "italic_12";
    public final static String BOLD_10 = "bold_10";
    public final static String BOLD_9 = "bold_9";
    public final static String BOLD_10_CL255_204_153 = "bold_10_cl255_204_153";
    public final static String DATA = "data";
    public final static String DATA_CURRENCY = "dat currency";
    public final static String DATA_NONE_BORDER = "data_none_border";
    public final static String BOLD_9_CL255_255_153 = "bold_9_cl255_204_153";
    public final static String BOLD_10_CL255_255_153 = "bold_10_cl255_255_153";
    public final static String BOLD_9_CL51_204_204 = "bold_9_cl51_204_204";
    public final static String BOLD_10_CL51_204_204 = "bold_10_cl51_204_204";
    public final static String BOLD_9_CL255_204_0 = "bold_9_cl255_204_0";
    public final static String BOLD_10_CL255_204_0 = "bold_10_cl255_204_0";
    public final static String BOLD_9_CL192_192_192 = "bold_9_cl192_192_192";
    public final static String BOLD_10_CL192_192_192 = "bold_10_cl192_192_192";
    public final static String BOLD_10_CL192_192_192_CENTER = "bold_10_cl192_192_192_center";
    public final static String BOLD_10_CL255_255_204 = "bold_10_cl255_255_204";
    public final static String BOLD_10_CL255_255_204_FORMAT_CURRENCY = "bold_10_cl255_255_204_format_currency";
    public final static String BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY = "bold_10_cl255_204_153_v2_format_currency";
    public final static String BOLD_10_CL255_204_153_V2 = "bold_10_cl255_204_153_v2";
    public final static String NOT_BOLD_11_RED = "not_bold_11_red";
    public final static String DATA_SMALL_TABLE = "data_small_table";
    public final static String CENTER = "center";
    private final static String FONT_ROMAN = "Times New Roman";

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
    public static void addCellsAndMerged(Sheet sheet, int colIndex, int rowIndex, int endColIndex, int endRowIndex, Object value, CellStyle cellFormat) {
        for (int i = rowIndex; i <= endRowIndex; i++) {
            Row row = sheet.getRow(i) == null ? sheet.createRow(i) : sheet.getRow(i);
            for (int j = colIndex; j <= endColIndex; j++) {
                if (i == rowIndex && j == colIndex) {
                    createCell(row, colIndex, value, cellFormat);
                }

            }
        }
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, endRowIndex, colIndex, endColIndex));
    }

    public static void createCell(Row row, int colIndex, Object value, CellStyle cellFormat){
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
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            }else if(value instanceof LocalDateTime) {
                cell.setCellValue(((LocalDateTime) value).toString());
            }else {
                cell.setCellValue((String) value);
            }
        }

        cell.setCellStyle(cellFormat);
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
    public static void addCell(Sheet sheet, int colIndex, int rowIndex, Object value, CellStyle cellFormat){
        Row row = sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);
        createCell(row, colIndex, value, cellFormat);
    }

    public static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        DataFormat fmt = wb.createDataFormat();
        /**Init Font */
        XSSFFont headerFontBold = (XSSFFont) wb.createFont();
        setFontPOI(headerFontBold, FONT_ROMAN, 15, true,true, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont titleBold = (XSSFFont) wb.createFont();
        setFontPOI(titleBold, FONT_ROMAN, 15, true,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont headerFont = (XSSFFont) wb.createFont();
        setFontPOI(headerFont, FONT_ROMAN, 11, false,true, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont italic_12 = (XSSFFont) wb.createFont();
        setFontPOI(italic_12, FONT_ROMAN, 12, false,true, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont bold_10 = (XSSFFont) wb.createFont();
        setFontPOI(bold_10, FONT_ROMAN, 10, true,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont data = (XSSFFont) wb.createFont();
        setFontPOI(data, FONT_ROMAN, 9, false,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont bold_9 = (XSSFFont) wb.createFont();
        setFontPOI(bold_9, FONT_ROMAN, 9, true,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        //////////////
        XSSFFont dataNoneBorder = (XSSFFont) wb.createFont();
        setFontPOI(dataNoneBorder, FONT_ROMAN, 11, false,false, new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0},null));
        /////////////
        XSSFFont x = (XSSFFont) wb.createFont();
        setFontPOI(x, FONT_ROMAN, 11, false,false, new XSSFColor(new byte[]{(byte)255, (byte)0, (byte)0},null));
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
        styleHeader9.setAlignment(HorizontalAlignment.RIGHT);
        styleHeader9.setVerticalAlignment(VerticalAlignment.CENTER);
        DataFormat dataFormat9 = wb.createDataFormat();
        styleHeader9.setDataFormat(dataFormat9.getFormat("#,###"));
        setBorderForCell(styleHeader8,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_10_CL255_204_153, styleHeader8);
        ////////////////////////////////////////////////////////////////////////
        /**data_style_10*/
        CellStyle styleHeader10 = wb.createCellStyle();
        styleHeader10.setFont(data);
        setBorderForCell(styleHeader10,BorderStyle.THIN, poiBlackNew);
        styles.put(DATA, styleHeader10);
        ////////////////////////////////////////////////////////////////////////
        /**data_none_border*/
        CellStyle styleData = wb.createCellStyle();
        styleData.setFont(dataNoneBorder);
        setBorderForCell(styleData,BorderStyle.NONE, poiBlackNew);
        styles.put(DATA_NONE_BORDER,styleData);
        ////////////////////////////////////////////////////////////////////////
        /**bold_9*/
        CellStyle styleHeader11 = wb.createCellStyle();
        styleHeader11.setFont(bold_9);

        styleHeader11.setAlignment(HorizontalAlignment.LEFT);
        styleHeader11.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader11,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_9, styleHeader11);
        ////////////////////////////////////////////////////////////////////////
        /**bold_9_style_12*/
        CellStyle styleHeader12 = wb.createCellStyle();
        styleHeader12.setFont(bold_9);
        XSSFCellStyle xSSFCellStyle12 = (XSSFCellStyle)styleHeader12;
        xSSFCellStyle12.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)153},null));
        xSSFCellStyle12.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        xSSFCellStyle12.setAlignment(HorizontalAlignment.CENTER);
        xSSFCellStyle12.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader12,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_9_CL255_255_153, styleHeader12);

        ///bold_10_style13 extends bold_9_style_12
        CellStyle styleHeader13 = ((XSSFCellStyle) styleHeader12);
        styleHeader13.setFont(bold_10);
        styles.put(BOLD_10_CL255_255_153, styleHeader13);

        /**bold_9_style_14*/
        CellStyle styleHeader14 = wb.createCellStyle();
        styleHeader14.setFont(bold_9);
        XSSFCellStyle xSSFCellStyle14 = (XSSFCellStyle)styleHeader14;
        xSSFCellStyle14.setFillForegroundColor(new XSSFColor(new byte[]{(byte)51, (byte)204, (byte)204},null));
        xSSFCellStyle14.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        xSSFCellStyle14.setAlignment(HorizontalAlignment.CENTER);
        xSSFCellStyle14.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader14,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_9_CL51_204_204, styleHeader14);

        ///bold_10_style_15 extends bold_9_style_14
        CellStyle styleHeader15 = ((XSSFCellStyle) styleHeader14);
        styleHeader15.setFont(bold_10);
        styles.put(BOLD_10_CL51_204_204, styleHeader15);

        /**bold_9_style_16*/
        CellStyle styleHeader16 = wb.createCellStyle();
        styleHeader16.setFont(bold_9);
        XSSFCellStyle xSSFCellStyle16 = (XSSFCellStyle)styleHeader16;
        xSSFCellStyle16.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255, (byte)204, (byte)0},null));
        xSSFCellStyle16.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        xSSFCellStyle16.setAlignment(HorizontalAlignment.CENTER);
        xSSFCellStyle16.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader16,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_9_CL255_204_0, styleHeader16);

        ///bold_10_style_17 extends bold_9_style_16
        CellStyle styleHeader17 = ((XSSFCellStyle) styleHeader16);
        styleHeader17.setFont(bold_10);
        styles.put(BOLD_10_CL255_204_0, styleHeader17);

        /**bold_9_style_18*/
        CellStyle styleHeader18 = wb.createCellStyle();
        styleHeader18.setFont(bold_9);
        XSSFCellStyle xSSFCellStyle18 = (XSSFCellStyle)styleHeader18;
        xSSFCellStyle18.setFillForegroundColor(new XSSFColor(new byte[]{(byte)192, (byte)192, (byte)192},null));
        xSSFCellStyle18.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        xSSFCellStyle18.setAlignment(HorizontalAlignment.LEFT);
        xSSFCellStyle18.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderForCell(styleHeader18,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_9_CL192_192_192, styleHeader18);

        ///bold_10_style_19 extends bold_9_style_18
        CellStyle styleHeader19 = ((XSSFCellStyle) styleHeader18);
        styleHeader19.setFont(bold_10);
        styles.put(BOLD_10_CL192_192_192, styleHeader19);

        CellStyle styleHeader26 = wb.createCellStyle();
        styleHeader26.setFont(bold_10);
        XSSFCellStyle xSSFCellStyle26 = (XSSFCellStyle)styleHeader26;
        xSSFCellStyle26.setFillForegroundColor(new XSSFColor(new byte[]{(byte)192, (byte)192, (byte)192},null));
        xSSFCellStyle26.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        xSSFCellStyle26.setAlignment(HorizontalAlignment.CENTER);
        xSSFCellStyle26.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(BOLD_10_CL192_192_192_CENTER, styleHeader26);

        /**bold_10_style_20 row total*/
        CellStyle styleHeader20 = wb.createCellStyle();
        styleHeader20.setFont(bold_10);
        XSSFCellStyle xSSFCellStyle20 = (XSSFCellStyle)styleHeader20;
        xSSFCellStyle20.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)204},null));
        xSSFCellStyle20.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorderForCell(styleHeader20,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_10_CL255_255_204, styleHeader20);
        //bold_10_style_21 fortmat currency row total
        CellStyle styleHeader21 = ((XSSFCellStyle) styleHeader20);
        DataFormat dataFormat21 = wb.createDataFormat();
        styleHeader21.setDataFormat(dataFormat21.getFormat("#,###"));
        styles.put( BOLD_10_CL255_255_204_FORMAT_CURRENCY, styleHeader21);

        //bold_10_style_22 fortmat currency
        CellStyle styleHeader22 = wb.createCellStyle();
        styleHeader22.setFont(data);
        DataFormat dataFormat22 = wb.createDataFormat();
        styleHeader22.setDataFormat(dataFormat22.getFormat("#,###"));
        setBorderForCell(styleHeader22,BorderStyle.THIN, poiBlackNew);
        styles.put(DATA_CURRENCY, styleHeader22);

        /**bold_10_style_23 row total*/
        CellStyle styleHeader23 = wb.createCellStyle();
        styleHeader23.setFont(bold_10);
        XSSFCellStyle xSSFCellStyle23 = (XSSFCellStyle)styleHeader23;
        xSSFCellStyle23.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255, (byte)204, (byte)153},null));
        xSSFCellStyle23.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorderForCell(styleHeader23,BorderStyle.THIN, poiBlackNew);
        styles.put(BOLD_10_CL255_204_153_V2, styleHeader23);
        //bold_10_style_24 fortmat currency row total
        CellStyle styleHeader24 = ((XSSFCellStyle) styleHeader23);
        DataFormat dataFormat24 = wb.createDataFormat();
        styleHeader24.setDataFormat(dataFormat24.getFormat("#,###"));
        styles.put( BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY, styleHeader24);

        /**not_bold_11_red */
        CellStyle styleHeader25 = wb.createCellStyle();
        styleHeader25.setFont(x);
        styleHeader25.setAlignment(HorizontalAlignment.LEFT);
        styleHeader25.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(NOT_BOLD_11_RED, styleHeader25);

        /**small_table*/
        CellStyle styleSmallTable = wb.createCellStyle();
        styleSmallTable.setFont(dataNoneBorder);
        DataFormat dataSmallTable = wb.createDataFormat();
        styleSmallTable.setDataFormat(dataSmallTable.getFormat("#,###"));
        styles.put(DATA_SMALL_TABLE, styleSmallTable);

        CellStyle center = wb.createCellStyle();
        center.setFont(data);
        center.setAlignment(HorizontalAlignment.CENTER);
        center.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(CENTER, center);


        return styles;

    }

    public static XSSFFont setFontPOI(XSSFFont fontStyle, String fontName, Integer fontHeight, Boolean isBold,Boolean isItalic, XSSFColor fontColor) {
        String fName = fontName == null ? "Arial" : fontName;
        Integer fHeight = fontHeight == null ? 9 : fontHeight;
        Boolean fIsBold = isBold == null ? false : isBold;
        Boolean fIsItalic = isItalic == null ? false : isItalic;
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

    public static void autoSizeAllColumns(SXSSFSheet sheet, int lastCol) {
        sheet.trackAllColumnsForAutoSizing();
        for (int x = 0; x <= lastCol; x++){
            sheet.autoSizeColumn(x);
        }
    }

    private static boolean isMergedCell(List<CellRangeAddress> ranges, int row, int column) {
        for (CellRangeAddress range : ranges) {
            if (range.isInRange(row, column)) {
                return true;
            }
        }

        return false;
    }
    private static Comment addComment(CreationHelper creationHelper,Drawing<Shape> drawing,RichTextString richTextString) {
        ClientAnchor clientAnchor = drawing.createAnchor(0, 0, 0, 0, 0, 2, 7, 12);
        Comment comment = drawing.createCellComment(clientAnchor);
        return comment;
    }
}
