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
@Table(name = "COMBO_PRODUCT_DETAIL")
public class ComboProductDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
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
