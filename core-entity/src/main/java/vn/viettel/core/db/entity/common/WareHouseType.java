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
@Table(name = "WAREHOUSE_TYPE")
public class WareHouseType extends BaseEntity {
    @Column(name = "WAREHOUSE_TYPE_CODE")
    private String wareHouseTypeCode;
    @Column(name = "WAREHOUSE_TYPE_NAME")
    private String wareHouseTypeName;
    @Column(name = "STATUS")
    private Integer status;
}
