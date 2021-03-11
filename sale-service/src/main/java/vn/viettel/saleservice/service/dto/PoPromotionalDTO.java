package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class PoPromotionalDTO extends BaseDTO{
    private String po_PromotionalNumber;
    private String poDate;
    private String poNote;
    private String status;
}
