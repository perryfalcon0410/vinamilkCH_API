package vn.viettel.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userAccount;
    private String firstName;
    private String lastName;
    @ApiModelProperty(notes = "Tên đầy đủ")
    private String fullName = "";
    private String phone;
    private String email;
    private Integer status;
    private String password;
    private Integer maxWrongTime;
    private Integer wrongTime;
    private Integer passwordConfig;
    private String captcha;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    public String getFullName(){
        if(firstName != null) fullName = firstName;
        if(lastName != null) fullName = fullName + " " + lastName;

        return fullName.trim();
    }
}
