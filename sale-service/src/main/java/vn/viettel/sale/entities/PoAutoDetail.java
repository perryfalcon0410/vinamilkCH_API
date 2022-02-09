package vn.viettel.sale.entities;

import java.time.LocalDateTime;
import java.util.Date;

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
@Table(name = "PO_AUTO_DETAIL")
public class PoAutoDetail {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Column(name = "PO_AUTO_ID")
	private Long poAutoId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
	@Column(name = "PRICE_ID")
	private Long priceId;
	@Column(name = "PRICE")
	private Long price;
	@Column(name = "PO_AUTO_DATE")
	private Date poAutoDate;
	@Column(name = "OPEN_STOCK")
	private Long openStock;
	@Column(name = "IMPORT")
	private Long importNumber;
	@Column(name = "EXPORT")
	private Long exportNumber;
	@Column(name = "STOCK")
	private Long stock;
	@Column(name = "MONTH_CUMULATE")
	private Long monthCumulate;
	@Column(name = "MONTH_PLAN")
	private Long monthPlan;
	@Column(name = "DAY_PLAN")
	private Long dayPlan;
	@Column(name = "DAY_RESERVE_REAL")
	private Long dayReserveReal;
	@Column(name = "SAFETY_STOCK_MIN")
	private Long safetyStockMin;
	@Column(name = "LEAD")
	private Long lead;
	@Column(name = "NEXT")
	private Long next;
	@Column(name = "REQUIMENT_STOCK")
	private Long requimentStock;
	@Column(name = "STOCK_PO_CONFIRM")
	private Long stockPoConfirm;
	@Column(name = "STOCK_PO_DVKH")
	private Long stockPoDvkh;
	@Column(name = "CONVFACT")
	private Long convfact;
	@Column(name = "QUANTITY")
	private Long quantity;
	@Column(name = "AMOUNT")
	private Long amount;
	@Column(name = "WARNING")
	private String warning;
	@Column(name = "DAY_RESERVE_PLAN")
	private Float dayReservePlan;
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt;
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	@Column(name = "CREATED_BY")
	private String createdBy;
}
