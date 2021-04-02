package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReceiptImportListDTO {
    private Long id;
    private String transCode;

    public ReceiptImportListDTO(Long id, String transCode) {
        this.id = id;
        this.transCode = transCode;
    }
}
