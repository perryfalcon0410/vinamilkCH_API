package vn.viettel.report.service.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SalesByCategoryReportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @Column(name = "CUSTOMER_ADDRESS")
    private String customerAddress;
    @Column(name = "COUNT_ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "TOTAL")
    private String total;
}
