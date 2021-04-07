package vn.viettel.core.security.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserContext {
    private String role;
    private Long roleId;
    private Long userId;
    private Long shopId;
}
