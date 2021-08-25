package vn.viettel.core.dto.customer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;


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
    private Long wareHouseTypeId;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private Integer status;

    public CustomerTypeDTO(Long id, String code, String name, Integer kaCustomerType, Integer posModifyCustomer, Long wareHouseTypeId, Integer status) {
        setId(id);
        this.code = code;
        this.name = name;
        this.kaCustomerType = kaCustomerType;
        this.posModifyCustomer = posModifyCustomer;
        this.wareHouseTypeId = wareHouseTypeId;
        this.status = status;
    }

    /*
        Dùng cho lấy giá và loại kho
     */
    public CustomerTypeDTO(Long id,  Long wareHouseTypeId) {
        setId(id);
        this.wareHouseTypeId = wareHouseTypeId;
    }

}
