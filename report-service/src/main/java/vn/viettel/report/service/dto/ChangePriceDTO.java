package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class ChangePriceDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "TRANS_DATE")
    private LocalDateTime transDate;
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "QUANTITY")
    private Long quantity;
    @Column(name = "INPUT_PRICE")
    private Double inputPrice;
    @Column(name = "TOTAL_INPUT")
    private Double totalInput;
    @Column(name = "OUTPUT_PRICE")
    private Double outputPrice;
    @Column(name = "TOTAL_OUTPUT")
    private Double totalOutput;
    @Column(name = "PRICE_CHANGE")
    private Double priceChange;
}
