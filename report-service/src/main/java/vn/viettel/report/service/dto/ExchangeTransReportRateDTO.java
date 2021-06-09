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
public class ExchangeTransReportRateDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TOTAL_SALE")
    private Float totalSale;
    @Column(name = "EXCHANGE_RATE")
    private Float exchangeRate;
}
