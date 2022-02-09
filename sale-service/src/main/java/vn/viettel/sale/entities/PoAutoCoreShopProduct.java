package vn.viettel.sale.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "POAUTO_CORE_SHOP_PRODUCT")
public class PoAutoCoreShopProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "COREVALUE")
    private Integer coreValue;
}
