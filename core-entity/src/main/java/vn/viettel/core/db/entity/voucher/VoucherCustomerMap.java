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
@Table(name = "VOUCHER_CUSTOMER_MAP")
public class VoucherCustomerMap extends BaseEntity {
    @Column(name = "VOUCHER_PROGRAM_ID")
    private Long voucherProgramId;
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "CUSTOMER_TYPE_CODE")
    private String customerTypeCode;
    @Column(name = "STATUS")
    private Integer status;
}
