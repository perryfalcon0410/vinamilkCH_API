package vn.viettel.core.db.entity.common;

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
@Table(name = "PRICES")
public class Price extends BaseEntity {
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "PRICE_TYPE")
    private Integer priceType;
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "VAT")
    private Float vat;
    @Column(name = "TO_DATE")
    private Date toDate;
    @Column(name = "STATUS")
    private Integer status;
}
