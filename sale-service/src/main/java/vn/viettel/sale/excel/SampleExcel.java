package vn.viettel.sale.excel;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.core.utils.NameHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SampleExcel {
    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;

    private LocalDateTime date;
    String[] headers;
    public SampleExcel( LocalDateTime date) {
        workbook = new SXSSFWorkbook();

        this.date = date;
    }
    private void writeHeaderLine() {
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        int col = 0,col_=4,row =0,col__=0;
        int colm = 9,rowm =0;
        sheet = workbook.createSheet("Sheet1");
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10);
        //header left
        headers = NameHeader.sampleInventoryHeader.split(";");
        if(null != headers && headers.length >0){
            for(String h : headers) {
                ExcelPoiUtils.addCell(sheet, col++, row , h, format1);
                if(h.equals("MÃ SP")){
                    ExcelPoiUtils.addCell(sheet, col__++, row + 1,"x", format);
                }else ExcelPoiUtils.addCell(sheet, col__++, row + 1,"", format);

            }
        }

    }

    public  ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        ByteArrayInputStream response = new ByteArrayInputStream(out.toByteArray());
        workbook.close();
        out.close();
        return response;
    }
}
