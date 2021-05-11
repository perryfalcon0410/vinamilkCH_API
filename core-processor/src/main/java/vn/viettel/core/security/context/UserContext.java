package vn.viettel.core.security.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.DataPermissionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserContext {
    private String role;
    private Long roleId;
    private Long userId;
    private String userName;
    private Long shopId;
    private List<DataPermissionDTO> permissions;
}
