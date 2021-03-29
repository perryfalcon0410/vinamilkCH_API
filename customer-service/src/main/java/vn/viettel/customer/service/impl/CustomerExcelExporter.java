package vn.viettel.customer.service.impl;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.customer.repository.*;
import vn.viettel.customer.service.dto.CustomerDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CustomerExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<CustomerDTO> customerList;

    public CustomerExcelExporter(List<CustomerDTO> customerList) {
        this.customerList = customerList;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Customers");

        Row header = sheet.createRow(0);
        Row row = sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.NO_FILL);

        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        row.setRowStyle(style);

        CellStyle styleHeader = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        fontHeader.setFontHeight(20);
        fontHeader.setFontName("Times New Roman");
        styleHeader.setFont(fontHeader);

        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);

        createCell(header, 8, "DANH SÁCH KHÁCH HÀNG", styleHeader);
        createCell(row, 0, "STT", style);
        createCell(row, 1, "Mã KH", style);
        createCell(row, 2, "Họ tên KH", style);
        createCell(row, 3, "Mã vạch", style);
        createCell(row, 4, "Ngày sinh", style);
        createCell(row, 5, "Giới tính", style);
        createCell(row, 6, "Nhóm KH", style);
        createCell(row, 7, "Trạng thái", style);
        createCell(row, 8, "KH riêng Cửa hàng", style);
        createCell(row, 9, "CMND", style);
        createCell(row, 10, "Ngày cấp", style);
        createCell(row, 11, "Nơi cấp", style);
        createCell(row, 12, "Di động", style);
        createCell(row, 13, "Email", style);
        createCell(row, 14, "Địa chỉ", style);
        createCell(row, 15, "Cơ quan", style);
        createCell(row, 16, "Địa chỉ cơ quan", style);
        createCell(row, 17, "Mã số thuế", style);
        createCell(row, 18, "Thẻ thành viên", style);
        createCell(row, 19, "Ngày cấp thẻ", style);
        createCell(row, 20, "Loại thẻ", style);
        createCell(row, 21, "Loại KH", style);
        createCell(row, 22, "Ngày tạo", style);
        createCell(row, 23, "Ghi chú", style);

        header.setHeight((short) 800);
        row.setHeight((short) 650);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 2;
        int stt = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);

        for (CustomerDTO customer : customerList) {
            stt++;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
//            MemberCard memberCard = memberCardRepository.findById(customer.getCardMemberId()).get();
//
//            createCell(row, columnCount++, stt, style);
//            createCell(row, columnCount++, customer.getCustomerCode(), style);
//            createCell(row, columnCount++, customer.getLastName() + " " + customer.getFirstName(), style);
//            createCell(row, columnCount++, customer.getBarCode(), style);
//            createCell(row, columnCount++, customer.getBirthday(), style);
//            createCell(row, columnCount++, customer.getGender(), style);
//
//            createCell(row, columnCount++, customer.getStatus(), style);
//            createCell(row, columnCount++, customer.getSpecialCustomer(), style);
//
//
//            createCell(row, columnCount++, customer.getPhoneNumber(), style);
//            createCell(row, columnCount++, customer.getEmail(), style);
//            createCell(row, columnCount++, customer.getAddress(), style);
//            createCell(row, columnCount++, customer.getCompanyName(), style);
//            createCell(row, columnCount++, customer.getCompanyAddress(), style);
//            createCell(row, columnCount++, customer.getTaxCode(), style);
//            createCell(row, columnCount++, memberCard.getMemberCardCode(), style);
//            createCell(row, columnCount++, memberCard.getCreatedAt(), style);
//
//            createCell(row, columnCount++, customer.getCreatedAt(), style);
//            createCell(row, columnCount++, customer.getNoted(), style);
        }
    }

    public ByteArrayInputStream export() throws IOException {
        writeHeaderLine();
        writeDataLines();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}