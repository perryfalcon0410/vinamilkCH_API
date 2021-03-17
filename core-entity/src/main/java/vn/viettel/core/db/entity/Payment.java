package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PAYMENTS")
public class Payment extends BaseEntity{
    @Column(name = "SALE_ORDER_ID")
    private Long saleOrderId;
    @Column(name = "TOTAL_PAYMENT")
    private int totalPayment;
    @Column(name = "CURRENCY")
    private String currency;
    @Column(name = "DISCOUNT")
    private int discount;
    @Column(name = "NEEDED_PAYMENT")
    private int neededPayment;
    @Column(name = "CUS_REAL_PAYMENT")
    private int customerRealPayment;
    @Column(name = "CHANGE_MONEY")
    private int changeMoney;
}

