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
@Table(name = "SHOP_PRODUCT")
public class ShopProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "CAT_ID")
    private Long catId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "MINSF")
    private String minSf;
    @Column(name = "MAXSF")
    private Integer maxSf;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "CREATE_USER")
    private String createUser;
}
