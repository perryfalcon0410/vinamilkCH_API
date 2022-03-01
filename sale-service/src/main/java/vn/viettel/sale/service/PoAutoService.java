package vn.viettel.sale.service;

import java.time.LocalDateTime;
import java.util.List;

import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;

public interface PoAutoService {

	public List<PoAutoDTO> getAllPoAuto (Long shopID, int page);
	
	public List<PoAutoDTO> getSearchPoAuto(String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus, Long shopId);
	
	public List<PoAutoDetailProduct> getPoAutoDetailProduct(String poAutoNumber, Long shopId);
	
	public int approvePoAuto(List<String> poAutoNumberList, Long shopId);
	
	public int cancelPoAuto(List<String> poAutoNumberList, Long shopId);
}
