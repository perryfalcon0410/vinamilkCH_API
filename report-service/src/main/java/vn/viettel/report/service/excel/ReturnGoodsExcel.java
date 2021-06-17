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

        ExcelPoiUtils.addCell(sheet1, 0, 8, "STT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 1, 8, "NHÓM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 2, 8, "NGÀNH HÀNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 3, 8, "MÃ SẢN PHẨM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 4, 8, "TÊN SẢN PHẨM", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 5, 8, "ĐVT", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 6, 8, "SỐ LƯỢNG", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 7, 8, "GIÁ BÁN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 8, 8, "THÀNH TIỀN", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 9, 8, "TIỀN TRẢ LẠI", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 10, 8, "NGÀY TRẢ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 11, 8, "LÝ DO TRẢ", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));
        ExcelPoiUtils.addCell(sheet1, 12, 8, "THÔNG TIN PHẢN HỒI", style.get(ExcelPoiUtils.BOLD_10_CL192_192_192));

        ExcelPoiUtils.addCell(sheet1, 0, 9, stt, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 1, 9, null, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 2, 9, null, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 3, 9, null, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 4, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 5, 9, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 6, 9, this.reportRequest.getTotalDTO().getTotalQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 7, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 8, 9, this.reportRequest.getTotalDTO().getTotalAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet1, 9, 9, this.reportRequest.getTotalDTO().getReturnCode(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet1, 10, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 11, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 12, 9, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));

        for (int i = 0; i < returnGoodsDTOS.size(); i++) {
            stt++;
            ExcelPoiUtils.addCell(sheet1, 0, rowMerge, stt , style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 1, rowMerge, 1, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 2, rowMerge, returnGoodsDTOS.get(i).getReturnCode() + "-" + returnGoodsDTOS.get(i).getReciept() + "-" + returnGoodsDTOS.get(i).getFullName(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 3, rowMerge, null, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 4, rowMerge, null, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 5, rowMerge, null, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 6, rowMerge, returnGoodsDTOS.get(i).getTotalQuantity(), style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 7, rowMerge, null, style.get(ExcelPoiUtils.DATA_CURRENCY));
            ExcelPoiUtils.addCell(sheet1, 8, rowMerge, returnGoodsDTOS.get(i).getTotalAmount(), style.get(ExcelPoiUtils.DATA_CURRENCY));
            ExcelPoiUtils.addCell(sheet1, 9, rowMerge, returnGoodsDTOS.get(i).getTotalRefunds(), style.get(ExcelPoiUtils.DATA_CURRENCY));
            ExcelPoiUtils.addCell(sheet1, 10, rowMerge, null, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 11, rowMerge, null, style.get(ExcelPoiUtils.DATA));
            ExcelPoiUtils.addCell(sheet1, 12, rowMerge, null, style.get(ExcelPoiUtils.DATA));
            for (ReturnGoodsDTO data : listArrayList.get(i)) {
                row = rowMerge;
                col = 0;
                row++;
                rowMerge++;
                stt++;
                ExcelPoiUtils.addCell(sheet1, col++, row, stt, style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, rowMerge, 1, style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getIndustry(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getProductCode(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getProductName(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getUnit(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getQuantity(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getPrice(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getAmount(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getRefunds(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, DateUtils.formatDate2StringDateTime(data.getPayDay()), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getReasonForPayment(), style.get(ExcelPoiUtils.DATA));
                ExcelPoiUtils.addCell(sheet1, col++, row, data.getFeedback(), style.get(ExcelPoiUtils.DATA));
            }
            rowMerge = row + 1;
        }
        ExcelPoiUtils.addCell(sheet1, 0, row + 1, stt + 1, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 1, row + 1, 2, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 2, row + 1, null, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 3, row + 1, null, style.get(ExcelPoiUtils.DATA));
        ExcelPoiUtils.addCell(sheet1, 4, row + 1, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 5, row + 1, "Tổng:", style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 6, row + 1, this.reportRequest.getTotalDTO().getTotalQuantity(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 7, row + 1, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 8, row + 1, this.reportRequest.getTotalDTO().getTotalAmount(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet1, 9, row + 1, this.reportRequest.getTotalDTO().getReturnCode(), style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2_FORMAT_CURRENCY));
        ExcelPoiUtils.addCell(sheet1, 10, row + 1, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 11, row + 1, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
        ExcelPoiUtils.addCell(sheet1, 12, row + 1, null, style.get(ExcelPoiUtils.BOLD_10_CL255_204_153_V2));
    }

    public ByteArrayInputStream export() throws IOException {
        this.writeHeaderLine();
        this.writeDataLines();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
