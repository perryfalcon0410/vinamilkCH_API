package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class InfosReturnDetailDTO {
    @ApiModelProperty(notes = "Ngày mua hàng")
    private Date orderDate;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String CustomerName;
    @ApiModelProperty(notes = "Lý do")
    private String reason;
    @ApiModelProperty(notes = "Mã kho kiểm kê")
    private String reasonDesc;
    @ApiModelProperty(notes = "Mã kho kiểm kê")
    private Date returnDate;
    @ApiModelProperty(notes = "Mã kho kiểm kê")
    private String userName;
    @ApiModelProperty(notes = "Mã kho kiểm kê")
    private String note;
}
