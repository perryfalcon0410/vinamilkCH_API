package vn.viettel.core.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class CustomerTypeDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã loại khách hàng")
    private String code;
    @ApiModelProperty(notes = "Tên loại khách hàng")
    private String name;
    @ApiModelProperty(notes = "Loại KA: 1-KA, 0-Khách hàng thường")
    private Integer kaCustomerType;
    @ApiModelProperty(notes = "1-Chỉnh sửa dưới CH, else chỉnh sửa trên HO")
    private Integer posModifyCustomer;
    @ApiModelProperty(notes = "Id loại kho")
    private Long wareHoseTypeId;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private Integer status;
}
