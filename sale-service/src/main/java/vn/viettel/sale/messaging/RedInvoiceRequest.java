package vn.viettel.sale.messaging;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import javax.persistence.Column;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedInvoiceRequest extends BaseRequest {


    @Column(name = "ID")
    private Long id;
    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;

}
