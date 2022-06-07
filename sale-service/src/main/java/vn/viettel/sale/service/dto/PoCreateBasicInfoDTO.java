package vn.viettel.sale.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoCreateBasicInfoDTO {

	private Integer dayInMonth;
	private Integer saleDayInMonth;
	private Integer salePlanInMonth;
}
