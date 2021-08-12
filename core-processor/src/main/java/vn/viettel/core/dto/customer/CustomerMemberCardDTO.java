package vn.viettel.core.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMemberCardDTO {

    @ApiModelProperty(notes = "Mã thẻ thành viên")
    private String memberCardCode;

    @ApiModelProperty(notes = "Tên thẻ")
    private String memberCardName;

    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;
}
