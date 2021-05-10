package vn.viettel.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userAccount;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Integer status;
    private String password;
    private Integer maxWrongTime;
    private Integer wrongTime;
    private Integer passwordConfig;
    private String captcha;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
}
