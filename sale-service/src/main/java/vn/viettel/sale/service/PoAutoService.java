package vn.viettel.sale.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;
import vn.viettel.sale.service.dto.ProductQuantityListDTO;
import vn.viettel.sale.service.dto.ProductStockDTO;
import vn.viettel.sale.service.dto.RequestOfferDTO;
import vn.viettel.sale.service.dto.PoCreateBasicInfoDTO;

public interface PoAutoService {

	public Page<PoAutoDTO> getAllPoAuto (Long shopID, int page);
	
	public Page<PoAutoDTO> getSearchPoAuto(String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus, Long shopId, int page);
	
	public PoCreateBasicInfoDTO getPoCreateBasicInfo(Long shopId);
	
	public List<PoAutoDetailProduct> getPoAutoDetailProduct(String poAutoNumber, Long shopId);
	
	public String approvePoAuto(List<String> poAutoNumberList, Long shopId);
	
	public String cancelPoAuto(List<String> poAutoNumberList, Long shopId);
	
	public List<ProductStockDTO> getProductByPage(Pageable pageable, Long shopId, String keyword);
	
	public String spiltPO( ProductQuantityListDTO productQuantityListDTO ,Long shopid);
	
	public List<RequestOfferDTO> getRequestPo(Long shopId);
}
