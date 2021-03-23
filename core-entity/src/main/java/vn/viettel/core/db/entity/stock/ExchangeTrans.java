package vn.viettel.core.db.entity.stock;

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
@Table(name = "EXCHANGE_TRANS")
public class ExchangeTrans extends BaseEntity {
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "TRANS_DATE")
    private Date transDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "WAREHOUSE_TYPE_ID")
    private Long wareHouseTypeId;
    @Column(name = "REASON_ID")
    private Long reasonId;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
