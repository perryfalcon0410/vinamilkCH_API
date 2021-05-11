package vn.viettel.authorization.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Thông tin vai trò người dùng")
public class RoleDTO {
    private Long id;
    @ApiModelProperty(notes = "Tên vai trò")
    private String roleName;
    @ApiModelProperty(notes = "Danh sách cửa hàng thuộc quản lý của vai trò")
    private List<ShopDTO> shops;
}
