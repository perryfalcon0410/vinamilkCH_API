package vn.viettel.promotion.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "VOUCHER_SHOP_MAP")
public class VoucherShopMap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "VOUCHER_PROGRAM_ID")
    private Long voucherProgramId;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "STATUS")
    private Integer status;
}
