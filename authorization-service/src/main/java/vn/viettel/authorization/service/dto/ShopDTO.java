package vn.viettel.authorization.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Thông tin Cửa hàng")
public class ShopDTO {
    private Long id;
    @ApiModelProperty(notes = "Tên Cửa hàng")
    private String shopName;
    @ApiModelProperty(notes = "Địa Cửa hàng")
    private String address;
    @ApiModelProperty(notes = "SĐT Cửa hàng")
    private String phone;
    @ApiModelProperty(notes = "Email Cửa hàng")
    private String email;
    @ApiModelProperty(notes = "Có được chỉnh sửa đơn Online")
    private boolean isEditable;
    @ApiModelProperty(notes = "Có được tạo tay đơn Online")
    private boolean isManuallyCreatable;

    public ShopDTO(Long id, String shopName, String address, String phone, String email) {
        this.id = id;
        this.shopName = shopName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
}
