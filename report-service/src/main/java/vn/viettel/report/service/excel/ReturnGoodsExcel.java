package vn.viettel.report.service.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.ExcelPoiUtils;
import vn.viettel.report.messaging.ChangeReturnGoodsReportRequest;
import vn.viettel.report.messaging.ReturnGoodsReportsRequest;
import vn.viettel.report.service.dto.ReturnGoodsDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReturnGoodsExcel {
    private static final String FONT_NAME = "Times New Roman";

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet1;

    private ShopDTO shopDTO;
    private ChangeReturnGoodsReportRequest reportRequest;
    private List<ReturnGoodsReportTotalDTO> returnGoodsDTOS = new ArrayList<>();
    private List<List<ReturnGoodsDTO>> listArrayList = new ArrayList<>();
    ReturnGoodsReportsRequest filter;
    private int rowNum = 1;
    Map<String, CellStyle> style;

    public ReturnGoodsExcel(
            ShopDTO shopDTO, ChangeReturnGoodsReportRequest reportRequest, ReturnGoodsReportsRequest filter) {
        this.shopDTO = shopDTO;
        this.reportRequest = reportRequest;
        this.filter = filter;
        style = ExcelPoiUtils.createStyles(workbook);

        for (ReturnGoodsDTO returnGoodsDTO : reportRequest.getReturnGoodsDTOS()) {
            if (!returnGoodsDTOS.stream().anyMatch(e -> e.getReturnCode().equals(returnGoodsDTO.getReturnCode()))) {
                returnGoodsDTOS.add(new ReturnGoodsReportTotalDTO(returnGoodsDTO.getReturnCode(), returnGoodsDTO.getReciept(), returnGoodsDTO.getFullName()));
            }
        }
        for (ReturnGoodsReportTotalDTO totalDTO : returnGoodsDTOS) {
            int totalQuantity = 0;
            Float totalAmount = 0f;
            Float totalRefunds = 0f;
            List<ReturnGoodsDTO> dtoList = new ArrayList<>();
            for (ReturnGoodsDTO returnGoodsDTO : reportRequest.getReturnGoodsDTOS()) {
                if (returnGoodsDTO.getReturnCode().equals(totalDTO.getReturnCode())) {
                    dtoList.add(returnGoodsDTO);
                    totalQuantity += returnGoodsDTO.getQuantity();
                    totalAmount += returnGoodsDTO.getAmount();
                    totalRefunds += returnGoodsDTO.getRefunds();
                    rowNum++;
                }
            }
            totalDTO.setTotalQuantity(totalQuantity);
            totalDTO.setTotalAmount(totalAmount);
            totalDTO.setTotalRefunds(totalRefunds);
            listArrayList.add(dtoList);
        }
    }

    private void writeHeaderLine() {

        List<XSSFSheet> sheets = new ArrayList<>();
        sheet1 = workbook.createSheet("Hang_tra_lai");
        sheets.add(sheet1);


        for (XSSFSheet sheet : sheets) {
            int col = 0, row = 0, colm = 9, rowm = 0;

            ExcelPoiUtils.addCellsAndMerged(sheet, col, row, colm, rowm, shopDTO.getShopName(), style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, shopDTO.getAddress(), style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, ++row, colm, ++rowm, "Tel:" + " " + shopDTO.getPhone() + "  " + "Fax:" + " " + shopDTO.getFax(), style.get(ExcelPoiUtils.HEADER_LEFT));
            //header right
            ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 2, colm + 9, rowm - 2, "CÔNG TY CỔ PHẦN SỮA VIỆT NAM", style.get(ExcelPoiUtils.HEADER_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row - 1, colm + 9, rowm - 1, "Số 10 Tân Trào, Phường Tân Phú, Q7, Tp.HCM", style.get(ExcelPoiUtils.HEADER_LEFT));
            ExcelPoiUtils.addCellsAndMerged(sheet, col + 10, row, colm + 9, rowm, "Tel: (84.8) 54 155 555  Fax: (84.8) 54 161 226", style.get(ExcelPoiUtils.HEADER_LEFT));

            ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 3, colm + 15, rowm + 3, "BÁO CÁO DANH SÁCH HÀNG TRẢ LẠI", style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));
            ExcelPoiUtils.addCellsAndMerged(sheet, col, row + 5, colm + 15, rowm + 5, "TỪ NGÀY: "
                    + DateUtils.formatDate2StringDate(filter.getFromDate()) + " ĐẾN NGÀY: " + DateUtils.formatDate2StringDate(filter.getToDate()), style.get(ExcelPoiUtils.ITALIC_12));
        }
    }

    private void writeDataLines() {
        int stt = 1, col, row = 0;
        int rowMerge = 10;
        Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
        CellStyle format = style.get(ExcelPoiUtils.DATA);
        CellStyle formatBold = style.get(ExcelPoiUtils.BOLD_10_CL192_192_192);
        CellStyle formatBoldV2 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2);
        CellStyle formatBoldV3 = style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY);
        CellStyle formatCurrency = style.get(ExcelPoiUtils.DATA_CURRENCY);
        ExcelPoiUtils.addCell(sheet1, 0, 8, "STT", formatBold);
        ExcelPoiUtils.addCell(sheet1, 1, 8, "NHÓM", formatBold);
        ExcelPoiUtils.addCell(sheet1, 2, 8, "NGÀNH HÀNG", formatBold);
        ExcelPoiUtils.addCell(sheet1, 3, 8, "MÃ SẢN PHẨM", formatBold);
        ExcelPoiUtils.addCell(sheet1, 4, 8, "TÊN SẢN PHẨM", formatBold);
        ExcelPoiUtils.addCell(sheet1, 5, 8, "ĐVT", formatBold);
        ExcelPoiUtils.addCell(sheet1, 6, 8, "SỐ LƯỢNG", formatBold);
        ExcelPoiUtils.addCell(sheet1, 7, 8, "GIÁ BÁN", formatBold);
        ExcelPoiUtils.addCell(sheet1, 8, 8, "THÀNH TIỀN", formatBold);
        ExcelPoiUtils.addCell(sheet1, 9, 8, "TIỀN TRẢ LẠI", formatBold);
        ExcelPoiUtils.addCell(sheet1, 10, 8, "NGÀY TRẢ", formatBold);
        ExcelPoiUtils.addCell(sheet1, 11, 8, "LÝ DO TRẢ", formatBold);
        ExcelPoiUtils.addCell(sheet1, 12, 8, "THÔNG TIN PHẢN HỒI", formatBold);

        ExcelPoiUtils.addCell(sheet1, 0, 9, stt, format);
        ExcelPoiUtils.addCell(sheet1, 1, 9, null, format);
        ExcelPoiUtils.addCell(sheet1, 2, 9, null, format);
        ExcelPoiUtils.addCell(sheet1, 3, 9, null, format);
        ExcelPoiUtils.addCell(sheet1, 4, 9, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 5, 9, "Tổng:", formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 6, 9, this.reportRequest.getTotalDTO().getTotalQuantity(), formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 7, 9, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 8, 9, this.reportRequest.getTotalDTO().getTotalAmount(), formatBoldV3);
        ExcelPoiUtils.addCell(sheet1, 9, 9, this.reportRequest.getTotalDTO().getTotalRefunds(), formatBoldV3);
        ExcelPoiUtils.addCell(sheet1, 10, 9, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 11, 9, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 12, 9, null, formatBoldV2);

        for (int i = 0; i < returnGoodsDTOS.size(); i++) {
            stt++;
            ExcelPoiUtils.addCell(sheet1, 0, rowMerge, stt , format);
            ExcelPoiUtils.addCell(sheet1, 1, rowMerge, 1, format);
            ExcelPoiUtils.addCell(sheet1, 2, rowMerge, returnGoodsDTOS.get(i).getReturnCode() + "-" + returnGoodsDTOS.get(i).getReciept() + "-" + returnGoodsDTOS.get(i).getFullName(), format);
            ExcelPoiUtils.addCell(sheet1, 3, rowMerge, null, format);
            ExcelPoiUtils.addCell(sheet1, 4, rowMerge, null, format);
            ExcelPoiUtils.addCell(sheet1, 5, rowMerge, null, format);
            ExcelPoiUtils.addCell(sheet1, 6, rowMerge, returnGoodsDTOS.get(i).getTotalQuantity(), format);
            ExcelPoiUtils.addCell(sheet1, 7, rowMerge, null, formatCurrency);
            ExcelPoiUtils.addCell(sheet1, 8, rowMerge, returnGoodsDTOS.get(i).getTotalAmount(), formatCurrency);
            ExcelPoiUtils.addCell(sheet1, 9, rowMerge, returnGoodsDTOS.get(i).getTotalRefunds(), formatCurrency);
            ExcelPoiUtils.addCell(sheet1, 10, rowMerge, null, format);
            ExcelPoiUtils.addCell(sheet1, 11, rowMerge, null, format);
            ExcelPoiUtils.addCell(sheet1, 12, rowMerge, null, format);
            for (ReturnGoodsDTO data : listArrayList.get(i)) {
                row = rowMerge;
                col = 0;
                row++;
                rowMerge++;
                stt++;
                ExcelPoiUtils.addCell(sheet1, col++, row, stt, format);
                ExcelPoiUtils.addCell(sheet1, col++, rowMerge, 1, format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getIndustry(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getProductCode(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getProductName(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getUnit(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getQuantity(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getPrice(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getAmount(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getRefunds(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, DateUtils.formatDate2StringDateTime(data.getPayDay()), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getReasonForPayment(), format);
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getFeedback(), format);
            }
            rowMerge = row + 1;
        }
        ExcelPoiUtils.addCell(sheet1, 0, row + 1, stt + 1, format);
        ExcelPoiUtils.addCell(sheet1, 1, row + 1, 2, format);
        ExcelPoiUtils.addCell(sheet1, 2, row + 1, null, format);
        ExcelPoiUtils.addCell(sheet1, 3, row + 1, null, format);
        ExcelPoiUtils.addCell(sheet1, 4, row + 1, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 5, row + 1, "Tổng:", formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 6, row + 1, this.reportRequest.getTotalDTO().getTotalQuantity(), formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 7, row + 1, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 8, row + 1, this.reportRequest.getTotalDTO().getTotalAmount(), formatBoldV3);
        ExcelPoiUtils.addCell(sheet1, 9, row + 1, this.reportRequest.getTotalDTO().getTotalRefunds(), formatBoldV3);
        ExcelPoiUtils.addCell(sheet1, 10, row + 1, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 11, row + 1, null, formatBoldV2);
        ExcelPoiUtils.addCell(sheet1, 12, row + 1, null, formatBoldV2);
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
