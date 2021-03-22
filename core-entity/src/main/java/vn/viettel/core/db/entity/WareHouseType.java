package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "WARE_HOUSE_TYPES")
public class WareHouseType extends BaseEntity{
    @Column(name = "WARE_HOUSE_TYPE_CODE")
    private String wareHouseTypeCode;

    @Column(name = "WARE_HOUSE_TYPE_NAME")
    private String wareHouseTypeName;
    @Column(name = "STATUS")
    private String status;

}
