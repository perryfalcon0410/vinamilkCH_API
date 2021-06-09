package vn.viettel.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCardDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã thẻ thành viên")
    private String memberCardCode;
    @ApiModelProperty(notes = "Tên thẻ")
    private String memberCardName;
    @ApiModelProperty(notes = "Ngày tạo thẻ thành viên")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime memberCardIssueDate;
    @ApiModelProperty(notes = "Id loại khách hàng")
    private Long closelyTypeId;
    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 2-Dự thảo, 0-Ngưng hoạt động")
    private Integer status;
    @ApiModelProperty(notes = "Id Loại thẻ")
    private Long cardTypeId;
}
