package vn.viettel.sale.service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

@Getter
@Setter
@NoArgsConstructor
public class RequestOfferDTO {
	
	private String MH;
	private String TH;
	private Long TDK;
	private Long Nhap;
	private Long Xuat;
	private Long LKTT;
	private Long KHTT;
	private Long DMKH;
	private Long DTTT;
	private Long min;
	private Long next;
	private Long lead;
	private Long YCT;
	private Integer HDD;
	private Integer QC;
	private Long SLT;
	private Long TT;
	private Long TL;
	private String CB;
}
