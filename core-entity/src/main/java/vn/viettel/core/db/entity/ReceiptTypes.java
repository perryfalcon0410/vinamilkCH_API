package vn.viettel.core.db.entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RECEIPT_TYPES")
public class ReceiptTypes extends BaseEntity{
    @Column(name = "TYPE")
    private String type;
    @Column(name = "CREATED_BY")
    private long created_by;
    @Column(name = "UPDATED_BY")
    private long updated_by;
    @Column(name = "DELETED_BY")
    private long deleted_by;
}
