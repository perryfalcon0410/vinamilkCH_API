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
@Table(name = "PERMISSIONS")
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PERMISSION_CODE")
    private String permissionCode;
    @Column(name = "PERMISSION_NAME")
    private String permissionName;
    @Column(name = "PERMISSION_TYPE")
    private Integer permissionType;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "IS_FULL_PRIVILEGE")
    private Integer isFullPrivilege;
    @Column(name = "STATUS")
    private Integer status;
}
