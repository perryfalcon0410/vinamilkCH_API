package vn.viettel.core.db.entity.voucher;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "VOUCHER_PROGRAM")
public class VoucherProgram extends BaseEntity {
    @Column(name = "VOUCHER_PROGRAM_CODE")
    private String voucherProgramCode;
    @Column(name = "VOUCHER_PROGRAM_NAME")
    private String voucherProgramName;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "TO_DATE")
    private Date toDate;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "OBJECT_NAME")
    private String objectName;

}
