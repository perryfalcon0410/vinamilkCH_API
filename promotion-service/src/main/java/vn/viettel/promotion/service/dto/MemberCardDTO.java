package vn.viettel.promotion.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime memberCardIssueDate;
    private Long closelyTypeId;
    @ApiModelProperty(notes = "Trạng thái hoạt động")
    private Integer status;
    @ApiModelProperty(notes = "id loại thẻ")
    private Long cardTypeId;
}
