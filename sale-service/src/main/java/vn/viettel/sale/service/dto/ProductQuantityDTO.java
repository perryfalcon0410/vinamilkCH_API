package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductQuantityDTO {
	
	private Long productId;
	
	private Integer productConv;
	
	private Long quantity;
	
	private Integer groupId;
	
}
