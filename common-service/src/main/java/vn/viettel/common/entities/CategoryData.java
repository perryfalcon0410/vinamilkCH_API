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
@Table(name = "CATEGORY_DATA")
public class CategoryData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "CATEGORY_CODE")
    private String categoryCode;
    @Column(name = "CATEGORY_NAME")
    private String categoryName;
    @Column(name = "CATEGORY_GROUP_CODE")
    private String categoryGroupCode;
    @Column(name = "REMARKS")
    private String remarks;
    @Column(name = "PARENT_CODE")
    private String parentCode;
    @Column(name = "FREE_FIELD1")
    private String freeField1;
    @Column(name = "FREE_FIELD2")
    private String freeField2;
    @Column(name = "STATUS")
    private Integer status;
}
