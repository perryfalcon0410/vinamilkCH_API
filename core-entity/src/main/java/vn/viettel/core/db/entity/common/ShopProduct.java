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
@Table(name = "SHOP_PRODUCT")
public class ShopProduct extends BaseEntity {
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
