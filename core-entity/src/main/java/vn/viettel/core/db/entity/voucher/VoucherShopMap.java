package vn.viettel.core.db.entity.voucher;

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
@Table(name = "VOUCHER_SHOP_MAP")
public class VoucherShopMap extends BaseEntity {
    @Column(name = "VOUCHER_PROGRAM_ID")
    private Long voucherProgramId;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "STATUS")
    private Integer status;
}
