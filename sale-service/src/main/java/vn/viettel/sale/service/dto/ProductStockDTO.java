package vn.viettel.sale.service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDTO {
	
	private String productName;
	
	private String productCode;
	
	private BigDecimal price;
	
	private BigDecimal quantity;
	
}
