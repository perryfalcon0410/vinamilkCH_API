package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalRedInvoice {
    private Float sumTotalQuantity = 0F;
    private Float sumTotalMoney = 0F;
    private Float sumAmountNotVat = 0F;
    private Float sumAmountGTGT = 0F;

    public TotalRedInvoice addTotalQuantity(Float totalQuantity) {
        this.sumTotalQuantity += totalQuantity;
        return this;
    }
    public TotalRedInvoice addTotalMoney(Float totalMoney) {
        this.sumTotalMoney += totalMoney;
        return this;
    }
    public TotalRedInvoice addAmountNotVat(Float amountNotVat) {
        this.sumAmountNotVat += amountNotVat;
        return this;
    }
    public TotalRedInvoice addAmountGTGT(Float amountGTGT) {
        this.sumAmountGTGT += amountGTGT;
        return this;
    }
}
