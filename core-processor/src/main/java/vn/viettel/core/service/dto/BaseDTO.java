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

//    public Timestamp getCreatedAt() {
//        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
//        if(createdAt != null)
//            return new Timestamp(createdAt.getTime() + (1000 * zoneOffset.getTotalSeconds()));
//        return null;
//    }
//
//    public Timestamp getUpdatedAt() {
//        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
//        if(updatedAt != null)
//            return new Timestamp(updatedAt.getTime() + (1000 * zoneOffset.getTotalSeconds()));
//        return null;
//    }
}
