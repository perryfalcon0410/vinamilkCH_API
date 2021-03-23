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
@Table(name = "ORG_ACCESS")
public class OrgAccess extends BaseEntity {
    @Column(name = "PERMISSION_ID")
    private Long permissionId;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "STATUS")
    private Integer status;
}
