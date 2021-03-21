package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "PRODUCT_TYPES")
public class ProductType extends BaseEntity{
    @Column(name = "NAME")
    private String name;

    @Column(name = "SLUG")
    private String slug;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "DELETED_BY")
    private Long deletedBy;
}
