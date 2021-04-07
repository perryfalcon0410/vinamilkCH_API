package vn.viettel.core.security.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.status.Object;

@Getter
@Setter
@NoArgsConstructor
public class UserContext {
    private String role;
    private Long roleId;
    private Long userId;
    private Object object;
    private Long objectId;
}
