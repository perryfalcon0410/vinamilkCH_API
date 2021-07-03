package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.service.dto.CustomerTradeDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO DANH SÁCH KHÁCH HÀNG",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"NGÀY XUẤT:" + dateExport,style.get(ExcelPoiUtils.ITALIC_12));

    }

    private void writeDataLines() {
        int row = 8;
        int col = 0;
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        CellStyle format1 = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        ExcelPoiUtils.addCell(sheet,col++, row, "STT", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "MÃ KHÁCH HÀNG", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "TÊN", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "HỌ", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "LOẠI KHÁCH HÀNG", format1);

        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY SINH", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NĂM SINH", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI SINH", format1);

        ExcelPoiUtils.addCell(sheet,col++, row, "DIỆN THOẠI", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "DI ĐỘNG", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ EMAIL", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "FAX", format1);

        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "QUỐC GIA", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "THÀNH PHỐ", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "QUẬN/HUYỆN", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "XÃ/PHƯỜNG", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ NHÀ", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI CÔNG TÁC", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "ĐỊA CHỈ CÔNG TÁC", format1);

        ExcelPoiUtils.addCell(sheet,col++, row, "GIỚI TÍNH", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NHÓM MÁU", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "CHỦNG TỘC", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "TÔN GIÁO", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGHỀ NGHIỆP KHÁC", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGHỀ NGHIỆP", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "HÔN NHÂN", format1);

        ExcelPoiUtils.addCell(sheet,col++, row, "NHÓM KH", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "SỐ CMND", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY CẤP CMND", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI CẤP CMND", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "HỘ CHIẾU", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY CẤP HC", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY HẾT HẠN HC", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NƠI CẤP HC", format1);

        ExcelPoiUtils.addCell(sheet,col++, row, "GHI CHÚ", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY TẠO", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGƯỜI TẠO", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGÀY CẬP NHẬT", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "NGƯỜI CẬP NHẬT", format1);
        ExcelPoiUtils.addCell(sheet,col++, row, "DOANH SỐ TÍCH LŨY", format1);

        if(!customers.isEmpty()) {

            for(int i = 0; i<customers.size(); i++) {
                CustomerTradeDTO customer = customers.get(i);
                int colValue = 0;
                row++;

                ExcelPoiUtils.addCell(sheet,colValue++, row, i + 1, format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCustomerCode(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getFirstName(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getLastName(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCusTypeName(), format);

                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getBirthDay()), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getYearDob(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPlaceOfBirth(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPhone(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getMobiPhone(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getEmail(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getFax(), format);

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getAddress(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCountry(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getProvince(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getDistrict(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPrecinct(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getStreet(), format);

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getWorkingOffice(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getOfficeAddress(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getGender(), format);

                ExcelPoiUtils.addCell(sheet,colValue++, row, null, format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, null, format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, null, format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, null, format);

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getJob(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getMarital(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCusTypeName(), format);

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getIdNo(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getIdNoIssuedDate()), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getIdNoIssuedPlace(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPassportNo(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getPassportNoIssuedDate()), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getPassportNoExpiryDate()), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getPassportNoIssuedPlace(), format);

                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getNoted(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getCreatedAt()), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getCreatedBy(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, DateUtils.formatDate2StringDate(customer.getUpdatedAt()), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getUpdatedBy(), format);
                ExcelPoiUtils.addCell(sheet,colValue++, row, customer.getSaleAmount(), formatCurrency);

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
