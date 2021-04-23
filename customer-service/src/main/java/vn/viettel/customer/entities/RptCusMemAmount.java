package vn.viettel.customer.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RPT_CUS_MEM_AMOUNT")
public class RptCusMemAmount extends BaseEntity {
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "MEMBER_CARD_ID")
    private Long memberCardId;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "CUST_SHOP_ID")
    private Long custShopId;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "SCORE")
    private Integer score;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "RPT_DATE")
    private Date rptDate;
}
