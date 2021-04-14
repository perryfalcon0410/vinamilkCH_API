package vn.viettel.core.security.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.PermissionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserContext {
    private String role;
    private Long roleId;
    private Long userId;
    private Long shopId;
    private List<PermissionDTO> permissionList;
}
