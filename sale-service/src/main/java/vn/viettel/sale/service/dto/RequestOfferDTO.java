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
	
	private String maHang;
	private String tenHang;
	private Long tonDauKy;
	private Long nhap;
	private Long xuat;
	private Long luyKeTieuThu;
	private Long keHoachTieuThu;
	private Long dinhMucKeHoach;
	private Long duTruKeHoach;
	private Long duTruThucTe;
	private Long min;
	private Long next;
	private Long lead;
	private Long yeuCauTon;
	private Integer hangDiDuong;
	private Integer yeuCauDatHang;
	private Long soLuongThung;
	private Long thanhTien;
	private Long trongLuong;
	private String canhBao;
}
