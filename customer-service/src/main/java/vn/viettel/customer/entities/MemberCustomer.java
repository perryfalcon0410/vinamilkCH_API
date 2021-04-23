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
@Table(name = "MEMBER_CUSTOMER")
public class MemberCustomer extends BaseEntity {
    @Column(name = "MEMBER_CARD_ID")
    private Long memberCardId;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "SCORE_CUMULATED")
    private Integer scoreCumulated;
    @Column(name = "ISSUE_DATE")
    private Date issueDate;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "RPT_DATE")
    private Date rptDate;
    @Column(name = "IS_ASSIGN")
    private Boolean isAssign;
    @Column(name = "TOTAL_SCORE")
    private Integer totalScore;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}