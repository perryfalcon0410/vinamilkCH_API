package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDTO extends BaseDTO {
    @ApiModelProperty(notes = "Số hóa đơn")
    private String orderNumber;
    @ApiModelProperty(notes = "Số hóa đơn tham chiếu")
    private String orderNumberRef;
    @ApiModelProperty(notes = "Tên nhân viên")
    private String userName;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerNumber;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Tổng tiền trước chiết khấu")
    private Double amount;
    @ApiModelProperty(notes = "Tổng khuyến mãi")
    private Double totalPromotion;
    @ApiModelProperty(notes = "Tổng tiền")
    private Double total;
    @ApiModelProperty(notes = "Ngày trả hàng")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dateReturn;

    public OrderReturnDTO(String orderNumber, String orderNumberRef, Double amount, Double totalPromotion, Double total, LocalDateTime dateReturn){
        this.orderNumber = orderNumber;
        this.orderNumberRef = orderNumberRef;
        this.amount = amount;
        this.totalPromotion = totalPromotion;
        this.total = total;
        this.dateReturn = dateReturn;
    }

    //sum total
    public OrderReturnDTO(Double amount, Double totalPromotion, Double total){
        this.amount = amount;
        this.totalPromotion = totalPromotion;
        this.total = total;
    }
}
