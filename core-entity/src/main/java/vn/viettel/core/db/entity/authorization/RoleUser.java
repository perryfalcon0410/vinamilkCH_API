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
@Table(name = "ROLE_USER")
public class RoleUser extends BaseEntity {
    @Column(name = "ROLE_ID")
    private Long roleId;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "INHERIT_USER_ID")
    private Long inheritUserId;
    @Column(name = "STATUS")
    private Integer status;
}
