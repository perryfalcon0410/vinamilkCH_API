package vn.viettel.core.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.service.dto.PermissionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JwtTokenBody {
    private String role;
    private Long userId;
    private Object object;
    private Long objectId;
    private Long shopId;
    private Long roleId;
    private List<PermissionDTO> permissionList;
}
