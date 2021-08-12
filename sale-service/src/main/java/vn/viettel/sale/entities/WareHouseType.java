package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "WAREHOUSE_TYPE")
public class WareHouseType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "WAREHOUSE_TYPE_CODE")
    private String wareHouseTypeCode;
    @Column(name = "WAREHOUSE_TYPE_NAME")
    private String wareHouseTypeName;
    @Column(name = "STATUS")
    private Integer status;
}
