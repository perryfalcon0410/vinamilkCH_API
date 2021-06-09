package vn.viettel.core.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {

    @ApiModelProperty(name = "Id")
    private Long id;

    @ApiModelProperty(notes = "Ngày tạo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    @ApiModelProperty(notes = "Ngày cập nhật")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    @ApiModelProperty(notes = "Người tạo")
    private String createdBy;

    @ApiModelProperty(notes = "Người cập nhật")
    private String updatedBy;
}
