package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductQuantityDTO {
	
	private String productCode;
	
	private Integer productConv;
	
	private Long quantity;
	
	private Integer groupId;
	
}
