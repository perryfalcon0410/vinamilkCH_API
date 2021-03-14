package vn.viettel.core.db.entity;

import io.swagger.annotations.ApiModelProperty;
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
@Table(name = "PO_CONFIRMS")
public class POConfirm extends BaseEntity {

    @Column(name = "PO_NO")
    private String poNo;

    @Column(name = "PO_INTERNAL_NUMBER")
    private String internalNumber;

    @Column(name = "PO_DATE")
    private Timestamp poDate;

    @ApiModelProperty(notes = "0.da nhap hang, 1.chua nhap hang,2.ko nhap hang")
    @Column(name = "STATUS")
    private Integer status;

}
