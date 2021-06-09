package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.service.dto.CustomerTradeDTO;
import vn.viettel.report.utils.ExcelPoiUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomerTradeExcel {
    private XSSFWorkbook workbook = new XSSFWorkbook();
    Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
    private XSSFSheet sheet;
    ShopDTO shop;
    ShopDTO parentShop;
    List<CustomerTradeDTO> customers;

    public CustomerTradeExcel(ShopDTO shop, ShopDTO parentShop, List<CustomerTradeDTO> customers) {
        this.shop = shop;
        this.parentShop = parentShop;
        this.customers = customers;
    }

    private void writeHeaderLine()  {
        String dateExport = DateUtils.formatDate2StringDate(LocalDateTime.now());
        int col = 0, row =0, colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shop.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel:"+" "+shop.getPhone()+"  "+"Fax:"+" "+shop.getFax() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel:"+" "+parentShop.getPhone()+"  "+"Fax:"+" "+parentShop.getFax(),style.get(ExcelPoiUtils.HEADER_LEFT));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO DOANH SỐ THEO HÓA ĐƠN",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"NGÀY XUẤT:" + dateExport,style.get(ExcelPoiUtils.ITALIC_12));

    }

    private void writeDataLines() {
        int row = 8;
        int col = 0;
        ExcelPoiUtils.addCell(sheet,col++, row, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ KHÁCH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "HỌ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "LOẠI KHÁCH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY SINH", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NĂM SINH", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI SINH", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCell(sheet,col++, row, "DIỆN THOẠI", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "DI ĐỘNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ EMAIL", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "FAX", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "QUỐC GIA", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "THÀNH PHỐ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "QUẬN/HUYỆN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "XÃ/PHƯỜNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ NHÀ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI CÔNG TÁC", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ CÔNG TÁC", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCell(sheet,col++, row, "GIỚI TÍNH", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NHÓM MÁU", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "CHỦNG TỘC", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "TÔN GIÁO", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGHỀ NGHIỆP KHÁC", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGHỀ NGHIỆP", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "HÔN NHÂN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCell(sheet,col++, row, "NHÓM KH", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ CMND", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY CẤP CMND", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI CẤP CMND", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "HỘ CHIẾU", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY CẤP HC", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY HẾT HẠN HC", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI CẤP HC", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCell(sheet,col++, row, "GHI CHÚ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY TẠO", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGƯỜI TẠO", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY CẬP NHẬT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "NGƯỜI CẬP NHẬT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet,col++, row, "DOANH SỐ TÍCH LŨY", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        if(!customers.isEmpty()) {

            for(int i = 0; i<customers.size(); i++) {
                CustomerTradeDTO customer = customers.get(i);
                int colValue = 0;
                row++;

                ExcelPoiUtils.addCell(sheet,colValue++, row, i + 1, style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCustomerCode(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getFirstName(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getLastName(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCusTypeCode(), style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getBirthDay()), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getYearDob(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPlaceOfBirth(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPhone(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getMobiPhone(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getEmail(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getFax(), style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getAddress(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCountry(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getProvince(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getDistrict(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPrecinct(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getStreet(), style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getWorkingOffice(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getOfficeAddress(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getGender(), style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet,colValue++, row, null, style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, null, style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, null, style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getJob(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getMarital(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCusTypeName(), style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getIdNo(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getIdNoIssuedDate()), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getIdNoIssuedPlace(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPassportNo(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getPassportNoIssuedDate()), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getPassportNoExpiryDate()), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPassportNoIssuedPlace(), style.get(ExcelPoiUtils.DATA));

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getNoted(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getCreatedAt()), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCreatedBy(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getUpdatedAt()), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getUpdatedBy(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getSaleAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));

            }
        }
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
