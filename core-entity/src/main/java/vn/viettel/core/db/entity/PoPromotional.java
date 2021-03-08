package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "po_promotional")
public class PoPromotional extends BaseEntity{
    @Column(name = "po_promotional_number")
    private String po_PromotionalNumber;
    @Column(name = "po_date")
    private String poDate;
    @Column(name = "po_note")
    private String poNote;
    @Column(name = "status")
    private Integer status;
}
