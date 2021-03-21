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
@Table(name = "RECEIPT_EXPORT_ADJUSTEDS")
public class ReceiptExportAdjusted extends BaseEntity{
    @Column(name = "LICENSE_NUMBER")
    private String licenseNumber;
    @Column(name = "RECEIPT_EXPORT_ADJUSTED_DATE")
    private Timestamp receiptExportAdjustedDate;
    @Column(name = "NOTE")
    private String note;

}
