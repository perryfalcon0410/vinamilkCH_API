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
@Table(name = "po_confirm")
public class POConfirm extends BaseEntity {

    @Column(name = "po_no")
    private String poNo;

    @Column(name = "po_internal_number")
    private String internalNumber;

    @Column(name = "po_date")
    private LocalDateTime poDate;

    @ApiModelProperty(notes = "0.da nhap hang, 1.chua nhap hang,2.ko nhap hang")
    @Column(name = "status")
    private Integer status;

}
