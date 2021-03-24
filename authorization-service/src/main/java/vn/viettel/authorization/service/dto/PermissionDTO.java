package vn.viettel.authorization.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PermissionDTO {
    private Long shopId;
    private Boolean view;
    private Boolean create;
    private Boolean update;
    private Boolean delete;
}
