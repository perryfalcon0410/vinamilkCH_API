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
@Table(name = "PO_ADJUSTEDS")
public class POAdjusted extends BaseEntity{
    @Column(name = "PO_ADJUSTED_NUMBER")
    private String poAdjustedNumber;

    @Column(name = "PO_DATE")
    private Timestamp poDate;

    @Column(name = "PO_NOTE")
    private String poNote;
}
