package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RECEIPT_EXPORT_BORROWS")
public class ReceiptExportBorrow extends BaseEntity{
    @Column(name = "LICENSE_NUMBER")
    private String licenseNumber;
    @Column(name = "RECEIPT_EXPORT_BORROWS_DATE")
    private Timestamp receiptExportAdjustedDate;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "STATUS")
    private Integer status;
}
