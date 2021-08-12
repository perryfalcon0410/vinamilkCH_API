package vn.viettel.authorization.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ORG_ACCESS")
public class OrgAccess extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PERMISSION_ID")
    private Long permissionId;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "STATUS")
    private Integer status;
}
