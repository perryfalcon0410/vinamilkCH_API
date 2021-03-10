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
@Table(name = "product_types")
public class ProductType extends BaseEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "status")
    private int status;

    @Column(name = "created_by")
    private long createdBy;

    @Column(name = "updated_by")
    private long updatedBy;

    @Column(name = "deleted_by")
    private long deletedBy;
}
