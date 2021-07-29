package vn.viettel.sale.service.dto;

import com.poiji.annotation.ExcelCellName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StockCountingExcel {

    @ExcelCellName("STT")
    private Integer index;

    @ExcelCellName("NGÀNH HÀNG")
    private String productCategory;

    @ExcelCellName("NHÓM SP")
    private String productGroup;

    @ExcelCellName("MÃ SP")
    private String productCode;

    @ExcelCellName("TÊN SP")
    private String productName;

    @ExcelCellName("SL TỒN KHO")
    private Integer stockQuantity;

    @ExcelCellName("GIÁ")
    private Double price;

    @ExcelCellName("THÀNH TIỀN")
    private Double totalAmount;

    @ExcelCellName("SL PACKAGE KIỂM KÊ")
    private Double packetQuantity;

    @ExcelCellName("SL LẺ KIỂM KÊ")
    private Double unitQuantity;

    @ExcelCellName("TỔNG SỐ LƯỢNG KỂM KÊ")
    private Integer inventoryQuantity;

    @ExcelCellName("CHÊNH LỆCH")
    private Integer changeQuantity;

    @ExcelCellName("ĐVT PACKAGE")
    private String packetUnit;

    @ExcelCellName("SL QUY ĐỔI")
    private Integer convfact;

    @ExcelCellName("ĐVT LẺ")
    private String unit;

    @ExcelCellName("Lỗi")
    private String error;

    private Long productId;


    public StockCountingExcel(Long productId, String productCode, String productName, String productGroup, String productCategory,
                              Double price, Integer stockQuantity, String unit, String packetUnit, Integer convfact, Integer inventoryQuantity ){
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productGroup = productGroup;
        this.stockQuantity = stockQuantity;
        this.unit = unit;
        this.packetUnit = packetUnit;
        this.convfact = convfact;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
    }
}
