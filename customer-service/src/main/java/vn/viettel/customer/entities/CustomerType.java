package vn.viettel.customer.entities;

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
@Table(name = "CUSTOMER_TYPE")
public class CustomerType extends BaseEntity {
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "KA_CUS_TYPE")
    private Integer kaCustomerType;
    @Column(name = "POS_MODIFY_CUS")
    private Integer posModifyCustomer;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "STATUS")
    private Integer status;
}
