package vn.viettel.sale.service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO {

	private Integer transQty;
	private Integer stockAdjQty;
	private Integer stockBorQty;
	private Integer saleOrdQty;
	private Integer comboProTransQty;
}
