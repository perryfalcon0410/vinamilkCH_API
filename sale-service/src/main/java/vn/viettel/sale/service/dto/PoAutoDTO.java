package vn.viettel.sale.service.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PoAutoDTO {

	private String poAutoNumber;
	private String groupCode;
	private int status;
	private LocalDateTime createAt;
	private LocalDateTime approveDate;
	private Long amount;
}
