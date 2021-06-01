package vn.viettel.sale.service.dto;

import com.poiji.annotation.ExcelCellName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ExcelCellName("SL PACKET KIỂM KÊ")
    private Integer packetQuantity;

    @ExcelCellName("SL LẺ KIỂM KÊ")
    private Integer unitQuantity;

    @ExcelCellName("TỔNG SL KIỂM KÊ")
    private Integer inventoryQuantity;

    @ExcelCellName("CHÊNH LỆCH")
    private Integer changeQuantity;

    @ExcelCellName("ĐVT PACKET")
    private String packetUnit;

    @ExcelCellName("SL QUY ĐỔI")
    private Integer convfact;

    @ExcelCellName("ĐVT LẺ")
    private String unit;

    @ExcelCellName("Lỗi")
    private String error;

    private Long productId;
}
