package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RECEIPT_ONLINES")
public class ReceiptOnline extends BaseEntity{
    @Column(name = "RECEIPT_CODE")
    private String receiptCode;
    @Column(name = "STATUS")
    private int status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "DELETED_BY")
    private Long deletedBy;
}

