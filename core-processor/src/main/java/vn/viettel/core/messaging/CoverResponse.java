package vn.viettel.core.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoverResponse <D, T>{
    private D response;
    private T info;

    public CoverResponse(D stockCountingDTOS, T totalStockCounting) {
        this.response = stockCountingDTOS;
        this.info = totalStockCounting;
    }
}
