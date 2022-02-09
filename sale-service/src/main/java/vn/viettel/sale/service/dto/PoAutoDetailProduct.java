package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PoAutoDetailProduct {

	private String productName;
	private String productCode;
	private Long quantity;
	private Long convfact;
	private Long price;
	private Long amount;
	
}
