package vn.viettel.core.db.entity;


import io.swagger.annotations.ApiModelProperty;
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
    private String poBorrowNumber;

    @Column(name = "po_date")
    private LocalDateTime poDate;

    @Column(name = "po_note")
    private String poNote;

    @ApiModelProperty(notes = "0.da nhap hang, 1.chua nhap hang")
    @Column(name = "status")
    private Integer status;
}
