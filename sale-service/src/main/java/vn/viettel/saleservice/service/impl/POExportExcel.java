package vn.viettel.saleservice.service.impl;




import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.db.entity.POConfirm;
import vn.viettel.saleservice.service.dto.POConfirmDTO;
import vn.viettel.saleservice.service.dto.SoConfirmDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.poi.ss.util.CellUtil.createCell;

public class POExportExcel {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<SoConfirmDTO> soConfirms;
    private String UPLOADED_FOLDER = "C:/tmp/";
    public POExportExcel(List<SoConfirmDTO> soConfirms) {
        this.soConfirms = soConfirms;
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Customers");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        row.setRowStyle(style);

        createCell(row, 0, "Customer Code", style);
        createCell(row, 1, "Full Name", style);
        createCell(row, 2, "Phone", style);
        createCell(row, 3, "Gender", style);
        createCell(row, 4, "DOB", style);
        createCell(row, 5, "Status", style);
        createCell(row, 6, "Group", style);

    }
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (SoConfirmDTO soConfirm : soConfirms) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, soConfirm.getSoNo(), style);
            createCell(row, columnCount++, soConfirm.getProductCode(), style);
            createCell(row, columnCount++, soConfirm.getProductName(), style);
            createCell(row, columnCount++, soConfirm.getProductPrice(), style);
            createCell(row, columnCount++, soConfirm.getQuantity(), style);
            createCell(row, columnCount++, soConfirm.getPriceTotal(), style);
        }
    }
    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        Date date = new Date();
        Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
        File file = new File(UPLOADED_FOLDER +"/po_report-"+formatter.format(date)+ ".xlsx");
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file, false);
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
