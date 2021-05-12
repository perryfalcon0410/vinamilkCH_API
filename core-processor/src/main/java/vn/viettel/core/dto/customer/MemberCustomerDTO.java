package vn.viettel.core.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCustomerDTO extends BaseDTO {
    @ApiModelProperty(notes = "Id thẻ thành viên")
    private Long memberCardId;
    @ApiModelProperty(notes = "Id Khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Ngày tạo")
    private Date issueDate;
    @ApiModelProperty(notes = "Id Cửa hàng")
    private Long shopId;
}
