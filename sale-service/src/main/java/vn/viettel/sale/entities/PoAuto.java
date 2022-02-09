package vn.viettel.sale.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PO_AUTO")
public class PoAuto {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
	@Column(name = "PO_AUTO_NUMBER")
    private String poAutoNumber;
    @Column(name = "PO_AUTO_DATE")
    private LocalDateTime poAutoDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "STAFF_ID")
    private Long staffId;
    @Column(name = "AMOUNT")
    private Long amount;
    @Column(name = "DISCOUNT")
    private Long discount;
    @Column(name = "TOTAL")
    private Long total;
    @Column(name = "STATUS")
    private int status;
    @Column(name = "BILLTOLOCATION")
    private String billToLocation;
    @Column(name = "SHIPTOLOCATION")
    private String shipToLocation;
    @Column(name = "PAYMENTTERM")
    private String paymentTerm;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "IS_SYNC_FTP")
    private int isSynFtp;
    @Column(name = "APPROVED_DATE")
    private LocalDateTime approveDate;
    @Column(name = "GROUP_CODE")
    private String groupCode;
    @Column(name = "CREATED_AT")
    private LocalDateTime createAt;
    @Column(name = "UPDATED_AT")
    private LocalDateTime updateAt;
    @Column(name = "UPDATED_BY")
    private String updatedBy;
    @Column(name = "CREATED_BY")
    private String createdBy;
    
}
