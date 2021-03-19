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
@Table(name = "COMBO_PRODUCT_DETAILS")
public class ComboProductDetail extends BaseEntity{
    @Column(name = "COMBO_PRODUCT_ID")
    private Long comboProductId;
    @Column(name = "FACTOR")
    private Integer factor;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "STATUS")
    private Float status;

}
