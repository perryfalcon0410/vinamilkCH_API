package vn.viettel.authorization.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.PermissionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin đăng nhập")
public class LoginResponse {
    private Long id;
    @ApiModelProperty(notes = "Tên tài khoản người dùng")
    private String userAccount;
    @ApiModelProperty(notes = "SĐT người dùng")
    private String phone;
    @ApiModelProperty(notes = "Email người dùng")
    private String email;
    @ApiModelProperty(notes = "Tên người dùng")
    private String firstName;
    @ApiModelProperty(notes = "Họ người dùng")
    private String lastName;
    @ApiModelProperty(notes = "Trạng thái hoạt động của người dùng")
    private Integer status;
    @ApiModelProperty(notes = "Danh sách vai trò của người dùng")
    private List<RoleDTO> roles;
    @ApiModelProperty(notes = "Vai trò được chọn để đăng nhập")
    private RoleDTO usedRole;
    @ApiModelProperty(notes = "Cửa hàng được chọn để đăng nhập")
    private ShopDTO usedShop;
//    @ApiModelProperty(notes = "Danh sách giao diện người dùng được tương tác")
//    private List<PermissionDTO> permissions;

    @ApiModelProperty(notes = "Danh sách giao diện người dùng được tương tác")
    private List<FormDTO> forms;
}
