package vn.viettel.sale.service.dto;

import com.poiji.annotation.ExcelProperty;
import com.poiji.annotation.ExcelRow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockCountingExcel {
    @ExcelRow
    private int rowIndex;

    @ExcelProperty(propertyName = "STT")
    private Integer index;

    @ExcelProperty(propertyName = "NGÀNH HÀNG")
    private String productCategory;

    @ExcelProperty(propertyName = "NHÓM SP")
    private String productGroup;

    @ExcelProperty(propertyName = "MÃ SP")
    private String productCode;

    @ExcelProperty(propertyName = "TÊN SP")
    private String productName;

    @ExcelProperty(propertyName = "SL TỒN KHO")
    private Integer stockQuantity;

    @ExcelProperty(propertyName = "GIÁ")
    private Float price;

    @ExcelProperty(propertyName = "THÀNH TIỀN")
    private Float totalAmount;

    @ExcelProperty(propertyName = "SL PACKET KIỂM KÊ")
    private Integer packetQuantity;

    @ExcelProperty(propertyName = "SL LẺ KIỂM KÊ")
    private Integer unitQuantity;

    @ExcelProperty(propertyName = "TỔNG SL KIỂM KÊ")
    private Integer inventoryQuantity;

    @ExcelProperty(propertyName = "CHÊNH LỆCH")
    private Integer changeQuantity;

    @ExcelProperty(propertyName = "ĐVT PACKET")
    private String packetUnit;

    @ExcelProperty(propertyName = "SL QUY ĐỔI")
    private Integer convfact;

    @ExcelProperty(propertyName = "ĐVT LẺ")
    private String unit;

    @ExcelProperty(propertyName = "Lỗi")
    private String eror;
}
