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
@Table(name = "permissions")
public class Permission extends BaseEntity{
    @Column(name = "role_id")
    private int roleId;
    @Column(name = "function_id")
    private int functionId;
    @Column(name = "action_id")
    private int actionId;
}
