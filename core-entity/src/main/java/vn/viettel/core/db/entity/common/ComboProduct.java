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
@Table(name = "COMBO_PRODUCT")
public class ComboProduct extends BaseEntity {
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "REF_PRODUCT_ID")
    private Long refProductId;
    @Column(name = "NUM_PRODUCT")
    private Integer numProduct;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "STATUS")
    private Integer status;
}
