package vn.viettel.sale.service;

import java.time.LocalDateTime;
import java.util.List;

import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;

public interface PoAutoService {

	public List<PoAutoDTO> getAllPoAuto();
	
	public List<PoAutoDTO> getSearchPoAuto(String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus);
	
	public List<PoAutoDetailProduct> getPoAutoDetailProduct(Long poAutoId);
}