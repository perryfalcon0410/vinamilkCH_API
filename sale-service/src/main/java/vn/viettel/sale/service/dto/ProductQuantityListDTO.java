package vn.viettel.sale.service.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductQuantityListDTO {
	
	private List<ProductQuantityDTO> productQuantityList;
	
	public void add(ProductQuantityDTO a) {
		
		if(productQuantityList.isEmpty()) {
			productQuantityList = new ArrayList<ProductQuantityDTO>();
		}
		productQuantityList.add(a);
	}
}
