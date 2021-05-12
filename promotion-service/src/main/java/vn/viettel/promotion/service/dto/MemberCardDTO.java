package vn.viettel.promotion.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Thẻ thành viên")
public class MemberCardDTO extends BaseRequest {
    private Long id;
    @ApiModelProperty(notes = "Mã thẻ thành viên")
    private String memberCardCode;
    @ApiModelProperty(notes = "Ngày tạo thẻ")
    private Date memberCardIssueDate;
    private Long closelyTypeId;
    @ApiModelProperty(notes = "Trạng thái hoạt động")
    private Integer status;
    @ApiModelProperty(notes = "id loại thẻ")
    private Long cardTypeId;
}
