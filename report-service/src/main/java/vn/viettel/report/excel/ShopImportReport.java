// package vn.viettel.report.excel;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.ss.util.CellRangeAddress;
// import org.apache.poi.xssf.usermodel.*;
// import vn.viettel.authorization.entities.Shop;
// import vn.viettel.report.service.dto.ShopImportDTO;
// import vn.viettel.report.utils.ExcelPoiUtils;

// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.util.List;
// import java.util.Map;

// public class ShopImportReport {
//     private XSSFWorkbook workbook;
//     private XSSFSheet sheet;
//     private List<ShopImportDTO> shopImportDTOS;
//     private Shop shop;
//     public ShopImportReport(List<ShopImportDTO> stockCountingList) {
//         this.shopImportDTOS = shopImportDTOS;
//         this.shop = shop;
//         workbook = new XSSFWorkbook();
//     }
//     private void writeHeaderLine()  {
//         Map<String, CellStyle> style = ExcelPoiUtils.createStyles(workbook);
//         int col = 0,row =0;
//         sheet = workbook.createSheet("Sản phẩm");
//         ExcelPoiUtils.addCellsAndMerged(sheet,col,row,col+9,row,"nghia",style.get(ExcelPoiUtils.TITLE_LEFT_BOLD));

//     }
//     private void writeDataLines() {
//     }
//     public ByteArrayInputStream export() throws IOException {
//         writeHeaderLine();
//         writeDataLines();
//         ByteArrayOutputStream out = new ByteArrayOutputStream();
//         workbook.write(out);
//         return new ByteArrayInputStream(out.toByteArray());

//     }


// }
