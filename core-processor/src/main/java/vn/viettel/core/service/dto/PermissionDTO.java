package vn.viettel.core.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private String formCode;
    private String formName;
    private Long parentFormId;
    private String type;
    private String url;
    private int orderNumber;
    private int status;
    private int privilegeType;
    private List<ControlDTO> controls;
}
