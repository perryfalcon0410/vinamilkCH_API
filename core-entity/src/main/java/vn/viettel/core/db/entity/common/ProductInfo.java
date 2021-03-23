package vn.viettel.core.db.entity.common;

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
@Table(name = "PRODUCT_INFO")
public class ProductInfo extends BaseEntity {
    @Column(name = "PRODUCT_INFO_CODE")
    private String apParamCode;
    @Column(name = "PRODUCT_INFO_NAME")
    private String apParamName;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "STATUS")
    private Integer status;
}
