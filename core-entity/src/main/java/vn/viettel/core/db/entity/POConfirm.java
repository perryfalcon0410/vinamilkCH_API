package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "po_confirm")
public class POConfirm extends BaseEntity {

    @Column(name = "po_no")
    private String po_no;

    @Column(name = "po_internal_number")
    private String internal_number;

    @Column(name = "po_date")
    private LocalDateTime po_date;

}
