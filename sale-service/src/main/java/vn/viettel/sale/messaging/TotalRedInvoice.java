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
    private Double sumTotalQuantity = 0D;
    private Double sumTotalMoney = 0D;
    private Double sumAmountNotVat = 0D;
    private Double sumAmountGTGT = 0D;

    public TotalRedInvoice(Double sumTotalQuantity, Double sumTotalMoney){
        this.sumTotalQuantity = sumTotalQuantity == null ? 0 : (double)Math.round(sumTotalQuantity);
        this.sumTotalMoney = sumTotalMoney == null ? 0 : (double)Math.round(sumTotalMoney);
    }

    public TotalRedInvoice(Double sumAmountNotVat, Long sumAmountGTGT){
        this.sumAmountNotVat = sumAmountNotVat == null ? 0 : (double)Math.round(sumAmountNotVat);
        this.sumAmountGTGT = sumAmountGTGT == null ? 0 : (double)Math.round(sumAmountGTGT);
    }

    public TotalRedInvoice addTotalQuantity(Double totalQuantity) {
        this.sumTotalQuantity += totalQuantity;
        return this;
    }
    public TotalRedInvoice addTotalMoney(Double totalMoney) {
        this.sumTotalMoney += totalMoney;
        return this;
    }
    public TotalRedInvoice addAmountNotVat(Double amountNotVat) {
        this.sumAmountNotVat += amountNotVat;
        return this;
    }
    public TotalRedInvoice addAmountGTGT(Double amountGTGT) {
        this.sumAmountGTGT += amountGTGT;
        return this;
    }
}
