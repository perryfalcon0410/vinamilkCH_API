package vn.viettel.core.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOnlRequest {

    @ApiModelProperty(notes = "Họ khách hàng")
    private String firstName;

    @ApiModelProperty(notes = "Tên khách hàng")
    private String lastName;

    @ApiModelProperty(notes = "Số điện thoại di động")
    private String mobiPhone;

    @ApiModelProperty(notes = "Ngày sinh")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dob;

    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private String address;

    @ApiModelProperty(notes = "Trạng thái: 1-Hoạt động, 0-Ngưng hoạt động")
    private Integer status;
}
