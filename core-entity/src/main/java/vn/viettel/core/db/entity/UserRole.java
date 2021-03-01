package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_roles")
public class UserRole extends BaseEntity{
    @Column(name = "user_id")
    private int userId;
    @Column(name = "role_id")
    private int roleId;

    public UserRole(int userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

}
