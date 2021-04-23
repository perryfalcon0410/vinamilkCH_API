package vn.viettel.common.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "AP_PARAM")
public class ApParam extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "AP_PARAM_CODE")
    private String apParamCode;
    @Column(name = "AP_PARAM_NAME")
    private String apParamName;
    @Column(name = "VALUE")
    private String value;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "STATUS")
    private Integer status;
}
