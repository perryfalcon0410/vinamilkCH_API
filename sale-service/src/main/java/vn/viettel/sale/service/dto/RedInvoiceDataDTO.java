package vn.viettel.sale.service.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedInvoiceDataDTO extends BaseDTO {
    @ApiModelProperty(notes = "ID đơn hàng")
    private Long saleOrderId;
    @ApiModelProperty(notes = "ID sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Ngành hàng")
    private String groupVat;
    @ApiModelProperty(notes = "ĐVT1")
    private String uom1;
    @ApiModelProperty(notes = "ĐVT2")
    private String uom2;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Giá trước thuế")
    private Double price;
    @ApiModelProperty(notes = "Giá sau thuế")
    private Double priceNotVat;
    @ApiModelProperty(notes = "Thành tiền trước thuế")
    private Double amount;
    @ApiModelProperty(notes = "Thành tiền sau thế")
    private Double amountNotVat;
    @ApiModelProperty(notes = "Thuế")
    private Double vat;
    @ApiModelProperty(notes = "Thuế giá trị gia tăng")
    private Double valueAddedTax;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;

    //Không được sửa hàm này vì được sử dụng ở SaleOrderDetailRepository
    public RedInvoiceDataDTO(Long saleOrderId, String note, Long productId, String productCode, String productName, String uom1, String uom2, Double vat,
                             String groupVat, Integer quantity, Double price, Double priceNotVat, Double amount ){
        this.saleOrderId = saleOrderId;
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.uom1 = uom1;
        this.uom2 = uom2;
        this.quantity = quantity;
        this.price = price;
        this.priceNotVat = priceNotVat;
        this.groupVat = groupVat;
        this.amount = amount;
        this.vat = vat;
        this.note = note;
    }

    public Double getAmountNotVat(){
        if(getPriceNotVat() != null && getQuantity() != null)
            amountNotVat = getPriceNotVat() * getQuantity();
        return amountNotVat;
    }

    public Double getValueAddedTax(){
        if(getPriceNotVat() != null && getQuantity() != null && getVat() != null)
            valueAddedTax = ((getPriceNotVat() * getQuantity()) * getVat()) / 100;

        return valueAddedTax;
    }
}