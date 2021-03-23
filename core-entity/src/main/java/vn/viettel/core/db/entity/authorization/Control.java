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
@Table(name = "CONTROLS")
public class Control extends BaseEntity {
    @Column(name = "CONTROL_CODE")
    private String controlCode;
    @Column(name = "CONTROL_NAME")
    private String controlName;
    @Column(name = "FORM_ID")
    private Long formId;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "STATUS")
    private Integer status;
}
