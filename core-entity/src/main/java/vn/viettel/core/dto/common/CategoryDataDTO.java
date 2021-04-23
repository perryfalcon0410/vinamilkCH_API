package vn.viettel.core.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDataDTO extends BaseDTO {
    private String categoryCode;
    private String categoryName;
    private String categoryGroupCode;
    private String remarks;
    private String parentCode;
    private String freeField1;
    private String freeField2;
    private Integer status;
}
