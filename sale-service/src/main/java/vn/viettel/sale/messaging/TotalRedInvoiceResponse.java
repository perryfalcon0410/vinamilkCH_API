package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalRedInvoiceResponse {
    private Float totalQuantity = 0F;
    private Float totalAmount = 0F;
    private Float totalValueAddedTax = 0F;

    public TotalRedInvoiceResponse addTotalQuantity(Integer totalQuantity) {
        this.totalQuantity += totalQuantity;
        return this;
    }
    public TotalRedInvoiceResponse addTotalAmount(Float totalAmount) {
        this.totalAmount += totalAmount;
        return this;
    }
    public TotalRedInvoiceResponse addTotalValueAddedTax(Float totalValueAddedTax) {
        this.totalValueAddedTax += totalValueAddedTax;
        return this;
    }


}
