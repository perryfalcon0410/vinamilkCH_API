package vn.viettel.report.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
public class TotalReport {
    private Integer totalQuantity;
    private Integer totalPacketQuantity;
    private Integer totalUnitQuantity;
    private Float totalAmountNotVat;
    private Float totalAmount;
}
