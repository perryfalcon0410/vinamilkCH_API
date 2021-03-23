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
@Table(name = "FUNCTION_ACCESS")
public class FunctionAccess extends BaseEntity {
    @Column(name = "PERMISSION_ID")
    private Long permissionId;
    @Column(name = "FORM_ID")
    private Long formId;
    @Column(name = "CONTROL_ID")
    private Long controlId;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "SHOW_STATUS")
    private Integer showStatus;

}
