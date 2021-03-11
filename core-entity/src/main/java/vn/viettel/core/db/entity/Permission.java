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
@Table(name = "PERMISSIONS")
public class Permission extends BaseEntity{
    @Column(name = "ROLE_ID")
    private int roleId;
    @Column(name = "FUNCTION_ID")
    private int functionId;
    @Column(name = "ACTION_ID")
    private int actionId;
}
