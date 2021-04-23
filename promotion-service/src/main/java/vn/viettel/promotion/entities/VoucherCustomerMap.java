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
@Table(name = "VOUCHER_CUSTOMER_MAP")
public class VoucherCustomerMap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "VOUCHER_PROGRAM_ID")
    private Long voucherProgramId;
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "CUSTOMER_TYPE_CODE")
    private String customerTypeCode;
    @Column(name = "STATUS")
    private Integer status;
}
