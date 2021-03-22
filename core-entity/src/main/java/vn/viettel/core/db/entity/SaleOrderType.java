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
@Table(name = "SALE_ORDER_TYPES")
public class SaleOrderType extends BaseEntity{
    @Column(name = "NAME")
    private String name;
    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "DELETED_BY")
    private Long deletedBy;
}
