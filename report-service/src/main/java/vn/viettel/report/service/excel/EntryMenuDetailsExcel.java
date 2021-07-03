package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.EntryMenuDetailsReportsRequest;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class EntryMenuDetailsExcel {
    private static final String FONT_NAME= "Times New Roman";

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;

    private ShopDTO shopDTO;
    private List<EntryMenuDetailsDTO> entryMenuDetailsDTOS;
    private EntryMenuDetailsDTO entryMenuDetailsDTO;
    EntryMenuDetailsReportsRequest filter;
    Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);;
    CellStyle format = style.get(ExcelPoiUtils.DATA);
    CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
    CellStyle format2 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
    CellStyle format3 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY);
    CellStyle format4 = style.get(ExcelPoiUtils.DATA_CURRENCY);

    public EntryMenuDetailsExcel(
            ShopDTO shopDTO, List<EntryMenuDetailsDTO> entryMenuDetailsDTOS, EntryMenuDetailsDTO total, EntryMenuDetailsReportsRequest filter) {
        this.shopDTO = shopDTO;
        this.entryMenuDetailsDTOS = entryMenuDetailsDTOS;
        this.entryMenuDetailsDTO = total;

        this.filter = filter;
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
        createCell(sheet1, rowHeader, 0, "STT", format1);
        createCell(sheet1, rowHeader, 1, "SOPO", format1);
        createCell(sheet1, rowHeader, 2, "SONOIBO", format1);
        createCell(sheet1, rowHeader, 3, "SOHD", format1);
        createCell(sheet1, rowHeader, 4, "NGAYHD", format1);
        createCell(sheet1, rowHeader, 5, "NGAYTT", format1);
        createCell(sheet1, rowHeader, 6, "SOTIEN", format1);
        createCell(sheet1, rowHeader, 7, "HDKM", format1);



        if(!entryMenuDetailsDTOS.isEmpty()) {
            Row rowTotalHeader = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalHeader, 4, "Tổng:" ,format2);
            createCell(sheet1, rowTotalHeader, 5, null , format2);
            createCell(sheet1, rowTotalHeader, 6, this.entryMenuDetailsDTO.getTotalAmount() , format3);
            createCell(sheet1, rowTotalHeader, 7, null, format2);

            for (int i = 0; i < entryMenuDetailsDTOS.size(); i++) {
                int column = 0;
                Row rowValue = sheet1.createRow(rowTable++);
                EntryMenuDetailsDTO record = entryMenuDetailsDTOS.get(i);

                createCell(sheet1, rowValue, column++, i + 1, format);
                createCell(sheet1, rowValue, column++, record.getPoNumber(), format);
                createCell(sheet1, rowValue, column++, record.getInternalNumber(), format);
                createCell(sheet1, rowValue, column++, record.getRedInvoiceNo(), format);
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getBillDate()), format);
                createCell(sheet1, rowValue, column++, DateUtils.formatDate2StringDate(record.getDateOfPayment()), format);
                createCell(sheet1, rowValue, column++, record.getAmount(), format4);
                createCell(sheet1, rowValue, column++, record.getPromotionalOrders(),  format);
            }

            Row rowTotalFooter = sheet1.createRow(rowTable++);

            createCell(sheet1, rowTotalFooter, 4, "Tổng:", format2);
            createCell(sheet1, rowTotalFooter, 5, null , format2);
            createCell(sheet1, rowTotalFooter, 6, this.entryMenuDetailsDTO.getTotalAmount() , format3);
            createCell(sheet1, rowTotalFooter, 7, null, format2);

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
