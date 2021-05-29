package vn.viettel.core.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {

    @ApiModelProperty(name = "Id")
    private Long id;

    @ApiModelProperty(notes = "Ngày tạo")
    private Timestamp createdAt;

    @ApiModelProperty(notes = "Ngày cập nhật")
    private Timestamp updatedAt;

    @ApiModelProperty(notes = "Người tạo")
    private String createBy;

    @ApiModelProperty(notes = "Người cập nhật")
    private String updateBy;
}
