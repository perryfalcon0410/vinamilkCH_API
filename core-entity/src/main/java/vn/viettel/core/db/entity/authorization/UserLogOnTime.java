package vn.viettel.core.db.entity.authorization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "USER_LOG_ON_TIME")
public class UserLogOnTime extends BaseEntity {
    @Column(name = "LOG_CODE")
    private String logCode;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "ACCOUNT")
    private String account;
    @Column(name = "COMPUTER_NAME")
    private String computerName;
    @Column(name = "MAC_ADDRESS")
    private String macAddress;
}
