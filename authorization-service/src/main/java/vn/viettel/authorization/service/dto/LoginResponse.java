package vn.viettel.authorization.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String userAccount;
    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    private Integer status;
    private List<RoleDTO> roles;
    private RoleDTO usedRole;
    private ShopDTO usedShop;
    private List<PermissionDTO> permissions;
}
