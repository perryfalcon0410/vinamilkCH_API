package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.service.dto.SalesByCategoryReportDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SalesByCategoryExcel extends ExcelPoiUtils{
    private ShopDTO shopDTO;
    private ShopDTO parentShop;
    private SXSSFWorkbook workbook = new SXSSFWorkbook();
    private SXSSFSheet sheet;
    private SaleCategoryFilter filter;
    private SalesByCategoryReportDTO tableDynamicDTO;
    private Map<String, CellStyle> style = this.createStyles(workbook);

    public SalesByCategoryExcel(SaleCategoryFilter filter, ShopDTO shopDTO, SalesByCategoryReportDTO tableDynamicDTO, ShopDTO parentShop) {
        this.filter = filter;
        this.shopDTO = shopDTO;
        this.tableDynamicDTO = tableDynamicDTO;
        this.parentShop = parentShop;
    }

    private void writeHeaderLine()  {
        String fromDate = DateUtils.formatDate2StringDate(filter.getFromDate());
        String toDate = DateUtils.formatDate2StringDate(filter.getToDate());

        int col = 0, row =0, colm = 9, rowm =0;
        sheet = workbook.createSheet("Sheet1");
        //header left
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row,colm,rowm,shopDTO.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,shopDTO.getAddress() ,style.get(ExcelPoiUtils.HEADER_LEFT));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,++row,colm,++rowm,"Tel: " + (shopDTO.getPhone()!=null? shopDTO.getPhone():"") + " Fax: " + (shopDTO.getFax()!=null?shopDTO.getFax():"") ,style.get(ExcelPoiUtils.HEADER_LEFT));
        //header right
        if(parentShop != null) {
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-2,colm+9,rowm-2,parentShop.getShopName(),style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row-1,colm+9,rowm-1,parentShop.getAddress(),style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet,col+10,row,colm+9,rowm,"Tel: " + (parentShop.getPhone()!=null?parentShop.getPhone():"") + " Fax: " +(parentShop.getFax()!=null?parentShop.getFax():""),style.get(ExcelPoiUtils.HEADER_LEFT));
        }

        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+3,colm+15,rowm+3,"BÁO CÁO BÁN HÀNG THEO CAT",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
        ExcelPoiUtils.addCellsAndMerged(sheet,col,row+5,colm+15,rowm+5,"TỪ NGÀY: "+fromDate+"  ĐẾN NGÀY: "+toDate,style.get(ExcelPoiUtils.ITALIC_12));

    }

    private void writeDataLines() {
        int row = 8;
        int col = 0, lastCol = 0;
        CellStyle formatBold = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        ExcelPoiUtils.addCell(sheet, col++, row, "STT", formatBold);
        ExcelPoiUtils.addCell(sheet, col++, row, "MÃ KHÁCH HÀNG", formatBold);
        ExcelPoiUtils.addCell(sheet, col++, row, "TÊN KHÁCH HÀNG", formatBold);
        ExcelPoiUtils.addCell(sheet, col++, row, "ĐỊA CHỈ", formatBold);
        ExcelPoiUtils.addCell(sheet, col++, row, "TẦN SUẤT", formatBold);
        List<String> categoryHeader = tableDynamicDTO.getCategory();
        for (int i = 0; i < categoryHeader.size(); i++) {
            String cat = categoryHeader.get(i);
            ExcelPoiUtils.addCell(sheet, col++, row, cat, formatBold);
        }
        ExcelPoiUtils.addCell(sheet, col++, row, "Tổng cộng", formatBold);
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        List<Object[]> dataset = (List<Object[]>) tableDynamicDTO.getResponse();
        for (int i = 0; i < dataset.size(); i++) {
            row++;
            Object[] datas = dataset.get(i);
            ExcelPoiUtils.addCell(sheet, 0, row, i + 1, format);
            for (int j = 0; j < datas.length; j++) {
                ExcelPoiUtils.addCell(sheet, j + 1, row, datas[j], formatCurrency);
                if(j + 1 > lastCol) lastCol = j + 1;
            }
        }
        ExcelPoiUtils.autoSizeAllColumns(sheet, lastCol);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        if(tableDynamicDTO.getResponse() != null) {
            this.writeDataLines();
        }
        return this.getStream(workbook);
    }
}
