package vn.viettel.sale.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.PoAuto;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.PoAutoDetail;
import vn.viettel.sale.repository.PoAutoRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.PoAutoService;
import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;

@Service
public class PoAutoServiceImpl extends BaseServiceImpl<PoAuto, PoAutoRepository> implements PoAutoService {
	
	@Autowired 
	PoAutoRepository poAutoRepository;
	
	@Autowired
	ProductRepository productRepository;

	@Override
	public List<PoAutoDTO> getAllPoAuto() {

		List<PoAutoDTO> poAutoDTOList = new ArrayList<PoAutoDTO>();
		
		poAutoRepository.findAll().forEach((n) -> {
			poAutoDTOList.add(convertToDTO(n));
		});
		
		return poAutoDTOList;
	}

	@Override
	public List<PoAutoDTO> getSearchPoAuto(String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus) {
		
		List<PoAutoDTO> poAutoDTOList = new ArrayList<PoAutoDTO>();
		
		poAutoRepository.searchPoList(poAutoNumber, poGroupCode, fromCreateDate, toCreateDate, fromApproveDate, toApproveDate, poStatus).forEach((n) -> {
			poAutoDTOList.add(convertToDTO(n));
		});
		return poAutoDTOList;
	}
	
	@Override
	public List<PoAutoDetailProduct> getPoAutoDetailProduct(Long poAutoId) {
		
		List<PoAutoDetailProduct> poAutoDetailProductList = new ArrayList<PoAutoDetailProduct>(); 
		
		poAutoRepository.getPoAutoDetailById(poAutoId).forEach((n) -> {
			poAutoDetailProductList.add(convertPoAutoDetail(n));
		});
		
		return poAutoDetailProductList;
	}

	private PoAutoDTO convertToDTO(PoAuto oldPo) {
		
		PoAutoDTO newPo = new PoAutoDTO();
		
		if(oldPo.getPoAutoNumber() != null) 
			newPo.setPoAutoNumber(oldPo.getPoAutoNumber());
		if(oldPo.getGroupCode() != null) 
			newPo.setGroupCode(oldPo.getGroupCode());
		newPo.setStatus(oldPo.getStatus());
		if(oldPo.getAmount() != null) 
			newPo.setAmount(oldPo.getAmount());
		if(oldPo.getCreateAt() != null) 
			newPo.setCreateAt(oldPo.getCreateAt());
		if(oldPo.getApproveDate() != null) 
			newPo.setApproveDate(oldPo.getApproveDate());
		return newPo;
	}
	
	private PoAutoDetailProduct convertPoAutoDetail(PoAutoDetail poAutoDetail) {
		
		PoAutoDetailProduct po = new PoAutoDetailProduct();
		
		if(poAutoDetail.getQuantity() != null)
			po.setQuantity(poAutoDetail.getQuantity());
		if(poAutoDetail.getConvfact() != null)
			po.setConvfact(poAutoDetail.getConvfact());
		if(poAutoDetail.getPrice() != null)
			po.setPrice(poAutoDetail.getPrice());
		if(poAutoDetail.getAmount() != null)
			po.setAmount(poAutoDetail.getAmount());
		
		if(poAutoDetail.getProductId() == null)	return po;
		
		Product temp = productRepository.getById(poAutoDetail.getProductId());
		
		if(temp == null) return po;
		
		if(temp.getProductCode() != null)
			po.setProductCode(temp.getProductCode());
		if(temp.getProductName() != null)
			po.setProductName(temp.getProductName());
		
		return po;
	}

}
