package vn.viettel.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCustomerDTO extends BaseDTO {
    @ApiModelProperty(notes = "Id thẻ thành viên")
    private Long memberCardId;
    @ApiModelProperty(notes = "Id Khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Ngày tạo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime issueDate;
    @ApiModelProperty(notes = "Id Cửa hàng")
    private Long shopId;
}
