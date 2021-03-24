package vn.viettel.authorization.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String username;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private Integer active;
    private String lastLoginDate;
    private List<RoleDTO> roles;
    private String usedRole;
    private List<PermissionDTO> functions;
    private List<FormDTO> forms;
}
