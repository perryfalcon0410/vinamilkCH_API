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
@Table(name = "po_borrow")
public class POBorrow extends BaseEntity {
    @Column(name = "po_borrow_number")
    private String po_borrow_number;

    @Column(name = "po_date")
    private LocalDateTime po_date;

    @Column(name = "po_note")
    private String po_note;
}
