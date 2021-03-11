package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "PO_PROMOTIONALS")
public class PoPromotional extends BaseEntity{
    @Column(name = "PO_PROMOTIONAL_NUMBER")
    private String poPromotionalNumber;
    @Column(name = "PO_DATE")
    private String poDate;
    @Column(name = "PO_NOTE")
    private String poNote;
    @Column(name = "STATUS")
    private Integer status;
}
