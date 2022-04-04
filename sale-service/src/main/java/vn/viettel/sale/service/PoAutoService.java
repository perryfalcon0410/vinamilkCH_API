package vn.viettel.sale.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;
import vn.viettel.sale.service.dto.ProductQuantityListDTO;
import vn.viettel.sale.service.dto.ProductStockDTO;

public interface PoAutoService {

	public Page<PoAutoDTO> getAllPoAuto (Long shopID, int page);
	
	public Page<PoAutoDTO> getSearchPoAuto(String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus, Long shopId, int page);
	
	public List<PoAutoDetailProduct> getPoAutoDetailProduct(String poAutoNumber, Long shopId);
	
	public int approvePoAuto(List<String> poAutoNumberList, Long shopId);
	
	public int cancelPoAuto(List<String> poAutoNumberList, Long shopId);
	
	public Page<ProductStockDTO> getProductByPage(Pageable pageable, Long shopId, String keyword);
	
	public void spiltPO( ProductQuantityListDTO productQuantityListDTO ,Long shopid);
}
