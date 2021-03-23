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
@Table(name = "VOUCHER_SALE_PRODUCT")
public class VoucherSaleProduct extends BaseEntity {
    @Column(name = "VOUCHER_PROGRAM_ID")
    private Long voucherProgramId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "STATUS")
    private Integer status;
}
