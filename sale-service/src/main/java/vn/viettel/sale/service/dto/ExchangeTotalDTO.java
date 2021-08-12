package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeTotalDTO {
    private Integer totalQuantity;
    private Double totalAmount;

    public ExchangeTotalDTO (Long totalQuantity, Double totalAmount){
        if(totalQuantity != null) this.totalQuantity = totalQuantity.intValue();
        this.totalAmount = totalAmount;

    }

    public ExchangeTotalDTO (){
    }
}
