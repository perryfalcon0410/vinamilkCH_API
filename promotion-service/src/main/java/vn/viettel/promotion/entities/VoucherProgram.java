package vn.viettel.promotion.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "VOUCHER_PROGRAM")
public class VoucherProgram extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "VOUCHER_PROGRAM_CODE")
    private String voucherProgramCode;
    @Column(name = "VOUCHER_PROGRAM_NAME")
    private String voucherProgramName;
    @Column(name = "PROGRAM_NAME_NOT_ACCENT")
    private String programNameNotAccent;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "TO_DATE")
    private Date toDate;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "OBJECT_NAME")
    private String objectName;

}
