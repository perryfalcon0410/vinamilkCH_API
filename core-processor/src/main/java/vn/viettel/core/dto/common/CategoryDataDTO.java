package vn.viettel.core.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDataDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã danh mục")
    private String categoryCode;
    @ApiModelProperty(notes = "Tên danh mục")
    private String categoryName;
    @ApiModelProperty(notes = "Mã nhóm danh mục")
    private String categoryGroupCode;
    @ApiModelProperty(notes = "Nhận xét")
    private String remarks;
    @ApiModelProperty(notes = "Mã cha mẹ")
    private String parentCode;
    @ApiModelProperty(notes = "Cột miễn phí 1")
    private String freeField1;
    @ApiModelProperty(notes = "Cột miễn phí 2 ")
    private String freeField2;
    @ApiModelProperty(notes = "Trạng thái")
    private Integer status;
}
