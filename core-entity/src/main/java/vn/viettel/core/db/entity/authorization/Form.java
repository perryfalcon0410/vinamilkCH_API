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
@Table(name = "FORMS")
public class Form extends BaseEntity {
    @Column(name = "FORM_CODE")
    private String formCode;
    @Column(name = "FORM_NAME")
    private String formName;
    @Column(name = "PARENT_FORM_ID")
    private Long parentFormId;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "URL")
    private String url;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ORDER_NUMBER")
    private Integer orderNumber;
    @Column(name = "STATUS")
    private Integer status;
}
