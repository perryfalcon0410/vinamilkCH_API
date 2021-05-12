package vn.viettel.promotion.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Khách hàng có thẻ thành viên")
public class MemberCustomerDTO {
    @ApiModelProperty(notes = "id thẻ thành viên")
    private Long memberCardId;
    @ApiModelProperty(notes = "id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Ngày tạo thẻ")
    private Date issueDate;
    @ApiModelProperty(notes = "id cửa hàng")
    private Long shopId;
}
