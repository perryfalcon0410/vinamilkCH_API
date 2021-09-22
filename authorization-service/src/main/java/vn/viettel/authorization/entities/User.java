package vn.viettel.authorization.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "USER_ACCOUNT")
    private String userAccount;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "MAX_WRONG_TIME")
    private Integer maxWrongTime;
    @Column(name = "WRONG_TIME")
    private Integer wrongTime;
    @Column(name = "PASSWORD_CONFIG")
    private Integer passwordConfig;
    @Column(name = "CAPTCHA")
    private String captcha;
}