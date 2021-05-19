package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellsReportsFilter {
    private Long shopId;
    private String orderNumber;
    private Date fromDate;
    private Date toDate;
    private String productKW;
    private Integer collecter;
    private Integer salesChannel;
    private String customerKW;
    private String phoneNumber;
    private Float fromInvoiceSales;
    private Float toInvoiceSales;

}
