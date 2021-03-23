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
@Table(name = "COMBO_PRODUCT_DETAIL")
public class ComboProductDetail extends BaseEntity {
    @Column(name = "COMBO_PRODUCT_ID")
    private Long comboProductId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "FACTOR")
    private Float factor;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "STATUS")
    private Integer status;
}
