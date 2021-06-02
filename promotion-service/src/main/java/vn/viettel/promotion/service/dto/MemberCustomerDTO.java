package vn.viettel.promotion.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Khách hàng có thẻ thành viên")
public class MemberCustomerDTO {
    @ApiModelProperty(notes = "id thẻ thành viên")
    private Long memberCardId;
    @ApiModelProperty(notes = "id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Ngày tạo thẻ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime issueDate;
    @ApiModelProperty(notes = "id cửa hàng")
    private Long shopId;
}
