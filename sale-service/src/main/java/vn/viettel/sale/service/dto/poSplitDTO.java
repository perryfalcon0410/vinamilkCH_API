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
public class poSplitDTO {

	private BigDecimal groupId;
	
	private BigDecimal objectType;
	
	private BigDecimal objectId;
	
}
