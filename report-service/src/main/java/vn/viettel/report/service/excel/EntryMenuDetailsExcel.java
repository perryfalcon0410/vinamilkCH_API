package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.messaging.EntryMenuDetailsReportsRequest;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class EntryMenuDetailsExcel {
    private static final String FONT_NAME= "Times New Roman";

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;

    private ShopDTO shopDTO;
    private List<EntryMenuDetailsDTO> entryMenuDetailsDTOS;
    private EntryMenuDetailsDTO entryMenuDetailsDTO;
    EntryMenuDetailsReportsRequest filter;

    Map<String, CellStyle> style;
    public EntryMenuDetailsExcel(
            ShopDTO shopDTO, List<EntryMenuDetailsDTO> entryMenuDetailsDTOS, EntryMenuDetailsDTO total, EntryMenuDetailsReportsRequest filter) {
        this.shopDTO = shopDTO;
        this.entryMenuDetailsDTOS = entryMenuDetailsDTOS;
        this.entryMenuDetailsDTO = total;

        this.filter = filter;
        style = ExcelPoiUtils.createStyles(workbook);
    }

    private void writeHeaderLine()  {


        List<XSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("sheet1");
        sheets.add(sheet1);


        for(XSSFSheet sheet: sheets) {
            int col = 0,row =0, colm = 9, rowm =0;

            ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shopDTO.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shopDTO.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shopDTO.getPhone()+"  "+"Fax:"+" "+shopDTO.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
            //header right
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,"CÔNG TY CỔ PHẦN SỮA VIỆT NAM",style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,"Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM",style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226",style.get(ExcelPoiUtils.HEADER_LEFT));

            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BẢNG KÊ CHI TIẾT CÁC HÓA ĐƠN NHẬP HÀNG ",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "
                    + DateUtils.formatDate2StringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(filter.getToDate()),style.get(ExcelPoiUtils.ITALIC_12));

        }
    }

    private void createTableSheet1() {
        int rowTable = 8;

        Row rowHeader = sheet1.createRow(rowTable++);
        createCell(sheet1, rowHeader, 0, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 1, "SOPO", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 2, "SONOIBO", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 3, "SOHD", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 4, "NGAYHD", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 5, "NGAYTT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 6, "SOTIEN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        createCell(sheet1, rowHeader, 7, "HDKM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));



        if(!entryMenuDetailsDTOS.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalHeader, 4, "Tổng:" ,style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 5, null , style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalHeader, 6, this.entryMenuDetailsDTO.getTotalAmount() , style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalHeader, 7, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));


            for (int i = 0; i < entryMenuDetailsDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                EntryMenuDetailsDTO record = entryMenuDetailsDTOS.get(i);

                createCell(sheet1, rowValue, column++, i + 1, style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getPoNumber(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getInternalNumber(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getRedInvoiceNo(), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getBillDate()), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getDateOfPayment()), style.get(ExcelPoiUtils.DATA));
                createCell(sheet1, rowValue, column++, record.getAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));
                createCell(sheet1, rowValue, column++, record.getPromotionalOrders(),  style.get(ExcelPoiUtils.DATA));
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalFooter, 4, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 5, null , style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
            createCell(sheet1, rowTotalFooter, 6, this.entryMenuDetailsDTO.getTotalAmount() , style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
            createCell(sheet1, rowTotalFooter, 7, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));

        }

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
        this.createTableSheet1();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
