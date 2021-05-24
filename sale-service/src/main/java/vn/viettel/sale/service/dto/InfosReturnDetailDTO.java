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
    @ApiModelProperty(notes = "Ngày hóa đon")
    private Date orderDate;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String CustomerName;
    @ApiModelProperty(notes = "Lý do trả hàng")
    private String reason;
    @ApiModelProperty(notes = "Mô tả trả hàng")
    private String reasonDesc;
    @ApiModelProperty(notes = "Ngày trả lại")
    private Date returnDate;
    @ApiModelProperty(notes = "Tên nhân viên")
    private String userName;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
}
