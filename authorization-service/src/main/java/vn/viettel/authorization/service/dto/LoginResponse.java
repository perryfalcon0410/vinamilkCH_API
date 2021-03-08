package vn.viettel.authorization.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.Role;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String username;
    private String phoneNumber;
    private String email;
    private String DOB;
    private String firstName;
    private String lastName;
    private boolean active;
    private String lastLoginDate;
    private List<String> roles;
    private String usedRole;
    private List<FunctionResponse> functions;

}
