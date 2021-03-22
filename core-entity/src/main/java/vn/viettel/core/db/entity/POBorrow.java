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
@Table(name = "PO_BORROWS")
public class POBorrow extends BaseEntity {
    @Column(name = "PO_BORROW_NUMBER")
    private String poBorrowNumber;

    @Column(name = "SHOP_ID")
    private Long shopId;

    @Column(name = "TO_SHOP_ID")
    private Long toShopId;

    @Column(name = "PO_TYPE")
    private Integer poType;

    @Column(name = "PO_DATE")
    private Timestamp poDate;

    @Column(name = "PO_NOTE")
    private String poNote;

    @Column(name = "WARE_HOUSE_TYPE_ID")
    private Long wareHouseTypeId;

    @ApiModelProperty(notes = "0.da nhap hang, 1.chua nhap hang")
    @Column(name = "STATUS")
    private Integer status;
}
