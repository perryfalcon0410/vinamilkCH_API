package vn.viettel.sale.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.convert.XStreamTranslator;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.MOQShopProduct;
import vn.viettel.sale.entities.PalletShopProduct;
import vn.viettel.sale.entities.PoAuto;
import vn.viettel.sale.entities.PoAutoCoreShopProduct;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SalePlan;
import vn.viettel.sale.entities.ShopProduct;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.entities.PoAutoDetail;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.repository.MOQShopProductRepository;
import vn.viettel.sale.repository.PalletShopProductRepository;
import vn.viettel.sale.repository.PoAutoCoreShopProductRepository;
import vn.viettel.sale.repository.PoAutoDetailRepository;
import vn.viettel.sale.repository.PoAutoGroupRepository;
import vn.viettel.sale.repository.PoAutoRepository;
import vn.viettel.sale.repository.PoConfirmRepository;
import vn.viettel.sale.repository.PoTransDetailRepository;
import vn.viettel.sale.repository.PriceRepository;
import vn.viettel.sale.repository.ProductInfoRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleDayRepository;
import vn.viettel.sale.repository.SalePlanRepository;
import vn.viettel.sale.repository.ShopProductRepository;
import vn.viettel.sale.repository.StockAdjustmentTransDetailRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.PoAutoService;
import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;
import vn.viettel.sale.service.dto.ProductQuantityListDTO;
import vn.viettel.sale.service.dto.ProductStockDTO;
import vn.viettel.sale.service.dto.RequestOfferDTO;
import vn.viettel.sale.service.dto.poSplitDTO;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.ReportStockClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.util.ConnectFTP;
import vn.viettel.sale.xml.Line;
import vn.viettel.sale.xml.NewPO;
import vn.viettel.sale.xml.PO;
import vn.viettel.sale.xml.PODetail;
import vn.viettel.sale.xml.POHeader;

@Service
public class PoAutoServiceImpl extends BaseServiceImpl<PoAuto, PoAutoRepository> implements PoAutoService {
	
	@Autowired 
	PoAutoRepository poAutoRepository;
	
	@Autowired
	PoAutoDetailRepository poAutoDetailRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	PalletShopProductRepository palletShopProductRepository;
	
	@Autowired
	PoAutoGroupRepository poAutoGroupRepository;
	
	@Autowired
	PoTransDetailRepository poTransDetailRepository;
	
	@Autowired
	StockAdjustmentTransDetailRepository stockAdjustmentTransDetailRepository;
	
	@Autowired
	PriceRepository priceRepository;
	
	@Autowired
	SalePlanRepository salePlanRepository;
	
	@Autowired
	SaleDayRepository saleDayRepository;
	
	@Autowired
	StockTotalRepository stockTotalRepository;
	
	@Autowired
	ShopProductRepository shopProductRepository;
	
	@Autowired
	PoConfirmRepository poConfirmRepository;
	
	@Autowired
	ProductInfoRepository productInfoRepository;
	
	@Autowired
	MOQShopProductRepository moqShopProductRepository;
	
	@Autowired
	PoAutoCoreShopProductRepository poAutoCoreShopProductRepository;
	
	@Autowired
	WareHouseTypeRepository wareHouseTypeRepository;
	
    @Autowired
    ApparamClient apparamClient;
    
    @Autowired
    ShopClient shopClient;
    
    @Autowired
    ReportStockClient reportStockClient;
    
    @Value("${spring.application.name}")
    public String appName;
    
    XStreamTranslator xstream = XStreamTranslator.getInstance();
    
    private Class<?>[] classes = new Class[] { PO.class, NewPO.class, POHeader.class, PODetail.class, Line.class};

    String ERROR = ResponseMessage.ERROR.statusCodeValue();
    String SUCCESS = ResponseMessage.SUCCESSFUL.statusCodeValue();
    
    enum Status{
    	UNAPPROVE_STATUS(0),
    	APPROVE_STATUS(1),
    	CANCEL_STATUS(2);
    	
    	private final Integer code;
    	
		Status(int code) {
			this.code = code;
		}
		
		public int code() {
	        return code;
	    }
    }

	@Override
	public Page<PoAutoDTO> getAllPoAuto(Long shopId, int page) {

		Pageable pageable = PageRequest.of(page, 5, Sort.by("poAutoNumber"));
		Page<PoAutoDTO> po = poAutoRepository.findAllPo(shopId, pageable);
		
		return po;
	}

	@Override
	public Page<PoAutoDTO> getSearchPoAuto(String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus, Long shopId, int page) {
		
		Pageable pageable = PageRequest.of(page, 5, Sort.by("poAutoNumber"));
		
		return poAutoRepository.searchPoList(poAutoNumber, poGroupCode, fromCreateDate, toCreateDate, fromApproveDate, toApproveDate, poStatus, shopId, pageable);
	}
	
	@Override
	public List<PoAutoDetailProduct> getPoAutoDetailProduct(String poAutoNumber, Long shopId) {
		
		List<PoAutoDetailProduct> poAutoDetailProductList = new ArrayList<PoAutoDetailProduct>(); 
		
		PoAuto poAuto = poAutoRepository.getPoAutoBypoAutoNumber(poAutoNumber, shopId);
		if (poAuto != null && poAuto.getId() != null) {
			
			List<PoAutoDetail> pod = poAutoRepository.getPoAutoDetailById(poAuto.getId());
			
			if(pod != null && !pod.isEmpty()) {
				pod.forEach((n) -> {
					poAutoDetailProductList.add(convertPoAutoDetail(n));
				});
			}
		}
		
		return poAutoDetailProductList;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String approvePoAuto(List<String> poAutoNumberList, Long shopId) {
		
		if(poAutoNumberList.isEmpty()) return ERROR;
		
		try {
			List<PoAuto> poAutoSucList = new ArrayList<>();
			poAutoNumberList.forEach(n -> {
				PoAuto temp = poAutoRepository.getPoAutoBypoAutoNumber(n, shopId);
				if(Status.UNAPPROVE_STATUS.code.equals(temp.getStatus())) {
					poAutoRepository.approvePo(n, LocalDateTime.now(), shopId);
					poAutoSucList.add(temp);
				}
			});
			
			if(!poAutoSucList.isEmpty()) poAutoExportXML(poAutoSucList);
		}
		catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}
	
	@Override
    @Transactional(rollbackFor = Exception.class)
	public String cancelPoAuto(List<String> poAutoNumberList, Long shopId) {
		
		if(poAutoNumberList.isEmpty()) return ERROR;
		
		poAutoNumberList.forEach(n -> {
			PoAuto temp = poAutoRepository.getPoAutoBypoAutoNumber(n, shopId);
			if(!Status.CANCEL_STATUS.code.equals(temp.getStatus())) {
				poAutoRepository.cancelPo(n, LocalDateTime.now(), shopId);
			}
		});
		
		return SUCCESS;
	}
	
	@Override
	public Page<ProductStockDTO> getProductByPage(Pageable pageable, Long shopid, String keyword) {

		List<Tuple> productList = poAutoRepository.getProductByPage(shopid, keyword);
		
		if(productList == null || productList.isEmpty()) return null;
		
		List<ProductStockDTO> productStockDtos = productList.stream()
	            .map(t -> new ProductStockDTO(
	                    t.get(0, String.class), 
	                    t.get(1, String.class), 
	                    t.get(2, BigDecimal.class),
	                    t.get(3, BigDecimal.class)
	                    ))
	            .collect(Collectors.toList());
		
		final int start = (int)pageable.getOffset();
 		final int end = Math.min((start + pageable.getPageSize()), productStockDtos.size());
		final Page<ProductStockDTO> paging = new PageImpl<>(productStockDtos.subList(start, end), pageable, productStockDtos.size());
		
		return paging;
	}
	
	public String spiltPO( ProductQuantityListDTO productQuantityListDTO ,Long shopId) {
		
		List<poSplitDTO> poSplit = poAutoGroupRepository.getSplitPO(shopId);
			
		ProductQuantityListDTO productQuantityListDtoPallet = new ProductQuantityListDTO();
			
		ProductQuantityListDTO productQuantityListDtoNotPallet = new ProductQuantityListDTO();
			
		List<String> palletSplitProductCodeList = productRepository.getPalletSplit(shopId);
			
		try {
				
			List<poSplitDTO> poSplitCat = poSplit.stream().filter(p -> p.getObjectType() == 0).collect(Collectors.toList());
			List<poSplitDTO> poSplitSubCat = poSplit.stream().filter(p -> p.getObjectType() == 1).collect(Collectors.toList());
			List<poSplitDTO> poSplitProd = poSplit.stream().filter(p -> p.getObjectType() == 2).collect(Collectors.toList());
			
			productQuantityListDTO.getProductQuantityList().forEach(n -> {
				if( palletSplitProductCodeList.contains(n.getProductCode()))
					productQuantityListDtoPallet.add(n);
				else
					productQuantityListDtoNotPallet.add(n);
			});
			
			handleSavePOAutoPOAutoDetail(productQuantityListDtoPallet, poSplitCat, poSplitSubCat, poSplitProd, shopId);
			
			handleSavePOAutoPOAutoDetail(productQuantityListDtoNotPallet, poSplitCat, poSplitSubCat, poSplitProd, shopId);
		}
		catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	@Override
	public List<RequestOfferDTO> getRequestPo(Long shopId) {
		
		List<RequestOfferDTO> res = new ArrayList<>();
		List<Product> pdList = productRepository.getByPalletAndMoq(shopId);
		
		pdList.forEach(pd -> {
			RequestOfferDTO temp = handleOfferPo(shopId, pd);
			if(temp != null) res.add(temp);
		});
		
		return res;
	}
	
	private RequestOfferDTO handleOfferPo(Long shopId, Product pd) {
		
		RequestOfferDTO reqPO = new RequestOfferDTO();
		WareHouseType warehouseType = wareHouseTypeRepository.getByCode("WHCH");
		if(warehouseType == null) return null;
		Long warehouseTypeId = warehouseType.getId();
		Long productId = pd.getId();
		String LUYKE = "F1_NGAY_LUYKE";
		
		LocalDate now = LocalDate.now();
		LocalDate firstMonthDay = now.withDayOfMonth(1);
		LocalDate yesterday =  now.minusDays(1);
		
		Response<ShopDTO> shopResp = shopClient.getByIdV1(shopId);
		if(shopResp == null || shopResp.getData() == null) return null;

		ShopDTO shop = shopResp.getData();
		
		if(pd.getStatus() != 1) return null;
		
		reqPO.setMaHang(pd.getProductCode());
		
		reqPO.setTenHang(pd.getProductName());
		
		Long TDK = 0l;
		LocalDate lastMonthDay = now.withDayOfMonth(1).minusDays(1);
		Response<Long> TDKresp = reportStockClient.getStockAggregated(shopId, productId, lastMonthDay);
		if(TDKresp != null) {
			TDK = TDKresp.getData();
		}
		reqPO.setTonDauKy(TDK);
		
		Long pastImportPO = 0l;
		Response<Long> pastImportPOresp = reportStockClient.getImport(shopId, productId, firstMonthDay, yesterday);
		if(pastImportPOresp != null && pastImportPOresp.getData() != null) {
			pastImportPO = pastImportPOresp.getData();
		}
		
		Long importPO1 = poAutoRepository.getImportQuantity1(shopId, now, LocalDateTime.now());
		Long importPO2 = poAutoRepository.getImportQuantity2(shopId, now);
		if(importPO1 == null) importPO1 = 0l;
		if(importPO2 == null) importPO2 = 0l;
		
		Long importPO = pastImportPO + importPO1 + importPO2;
		reqPO.setNhap(importPO);
		
		Long pastExportPO = 0l;
		Response<Long> pastExportPOresp = reportStockClient.getExport(shopId, productId, firstMonthDay, yesterday);
		if(pastExportPOresp == null) pastExportPO = 0l;
		else pastExportPO = pastExportPOresp.getData();
		if(pastExportPO == null) pastExportPO = 0l;
		
		Long exportPO1 = poAutoRepository.getExportQuantity1(shopId, now, LocalDateTime.now());
		Long exportPO2 = poAutoRepository.getExportQuantity2(shopId, now);
		if(exportPO1 == null) exportPO1 = 0l;
		if(exportPO2 == null) exportPO2 = 0l;
		
		Long exportPO = pastExportPO + exportPO1 + exportPO2;
		reqPO.setXuat(exportPO);
		
		Long pastLKTT = 0l;
		Response<Long> pastLKTTresp = reportStockClient.getCumulativeConsumption(shopId, productId, firstMonthDay, yesterday);			
		if(pastLKTTresp == null || pastLKTTresp.getData() == null) pastLKTT = 0l;
		else pastLKTT = pastLKTTresp.getData();
		
		Long presentLKTH = poAutoRepository.getComsumptionQuantity(shopId, now);
		if(presentLKTH == null) presentLKTH = 0l;
		
		Long LKTT = pastLKTT + presentLKTH;
		reqPO.setLuyKeTieuThu(LKTT);
		
		Long KHTT = salePlanRepository.getQuantityByShopProduct(shopId, productId, firstMonthDay);
		if(KHTT == null || KHTT == 0l) return null;
		reqPO.setKeHoachTieuThu(KHTT);

		Integer workingDayMonth = saleDayRepository.getDayMonthByShopId(shopId, firstMonthDay);
		if(workingDayMonth == null || workingDayMonth == 0) return null;
		//return ERROR + ": DMKH";
		Long DMKH = (Long) KHTT/workingDayMonth;
		reqPO.setDinhMucKeHoach(DMKH);

		Long DTTT = 0l;
		Integer stockDTparent = 0;
		Integer stockDT = stockTotalRepository.getStockTotalByShopProduct(shopId, warehouseTypeId, productId);
		if(stockDT == null || stockDT == 0) return null;
		
		Long DTKH = stockDT/DMKH;
		reqPO.setDuTruKeHoach(DTKH);
		
		Response<ShopParamDTO> shopParamDTO = shopClient.getShopParamV1(LUYKE, LUYKE, shopId);
		
		int breakCounter = 1;
		while (shopParamDTO == null && breakCounter < 4) {
			breakCounter++;
			shop = shop.getParentShop();
			if(shop == null) return null;
			shopParamDTO = shopClient.getShopParamV1(LUYKE, LUYKE, shop.getId());
		}
		
		ShopParamDTO data = shopParamDTO.getData();
		
		Long n = 28l;
		
		if(data != null) {
			if(data.getName() != null)
				try {
					n = Long.valueOf(data.getName());					
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
		}
		LocalDate beforeNday = now.minusDays(n);
		
		if(pd.getRefProductId() == null) {
			
			Long pastDTTT = 0l;
			Response<Long> pastDTTTresp = new Response<Long>();
			if(n != 0l) {
				pastDTTTresp = reportStockClient.getCumulativeConsumption(shopId, productId, beforeNday, yesterday);			
			}
			if(pastDTTTresp != null && pastDTTTresp.getData() != null) pastDTTT = pastDTTTresp.getData();
			
			Long presentDTTT = poAutoRepository.getComsumptionQuantity(shopId, now);
			if(presentDTTT == null) presentDTTT = 0l;
			
			DTTT = DTKH + presentDTTT + pastDTTT;
		}
		else {
			stockDTparent = stockTotalRepository.getStockTotalByShopProduct(shopId, warehouseTypeId, pd.getRefProductId());
			if(stockDTparent == null) stockDTparent = 0;
			Long stockDTTT = (long) (stockDT + stockDTparent);
			
			Long exportDT = 0l;
			Response<Long> exportDTResp =  reportStockClient.getExport(shopId, productId, beforeNday, yesterday);
			if(exportDTResp != null) exportDT = exportDTResp.getData();
			if(exportDT == null) exportDT = 0l;
			
			Long exportDTparent = 0l;
			Response<Long> exportDTparentResp =  reportStockClient.getExport(shopId, pd.getRefProductId(), beforeNday, yesterday);
			if(exportDTparentResp != null) exportDT = exportDTparentResp.getData();
			if(exportDTparent == null) exportDTparent = 0l;
			
			Long exportDTTT = exportDT + exportDTparent;
			if(exportDTTT == 0l) return null;
			
			DTTT = stockDTTT / exportDTTT * n;
		}
		reqPO.setDuTruThucTe(DTTT);
		
		Long minSf = 0l;
		Integer maxSf = 0;
		Long calendarDay = 0l;
		Long lead = 0l;
		Long percentage = 0l;
		ShopProduct spc = null;
		
		ShopProduct sp = shopProductRepository.findByShopIdAndProductIdAndType(shopId, productId, 2);
		if(sp == null || (sp.getMinSf() == null && sp.getCalendarDay() == null && sp.getLead() == null)) 
			spc = shopProductRepository.findByShopIdAndCatIdAndType(shopId, pd.getCatId(), 1);
		
		if(sp != null) {
			if(sp.getMinSf() != null) minSf = Long.valueOf(sp.getMinSf());
			if(spc != null) minSf = Long.valueOf(spc.getMinSf());
			if(minSf == null) minSf = 0l;
		
			calendarDay = sp.getCalendarDay();
			if(spc != null) calendarDay = spc.getCalendarDay();
			if(calendarDay == null) calendarDay = 0l;
		
			lead = sp.getLead();
			if(spc != null) lead = spc.getLead();
			if(lead == null) lead = 0l;
			
			percentage = sp.getPercentage();
			if(spc != null) percentage = spc.getPercentage();
			if(percentage == null) percentage = 0l;
			
			maxSf = sp.getMaxSf();
			if(spc != null) maxSf = spc.getMaxSf();
			if(maxSf == null) maxSf = 0;
		}
		reqPO.setMin(minSf);
		reqPO.setNext(calendarDay);
		reqPO.setLead(lead);

		Long YCT = (minSf + calendarDay + lead) * DMKH;
		reqPO.setYeuCauTon(YCT);
		
		Integer HDD = poConfirmRepository.getQuantityByShopIdAndStatusAndImportDate(shopId, 0, now);
		if(HDD == null) HDD = 0;
		reqPO.setHangDiDuong(HDD);
		
		Integer QC = pd.getConvFact();
		if(QC == null || QC == 0) return null;
		reqPO.setYeuCauDatHang(QC);
		
		Long YCDH = 0l;
		
		YCDH = ((minSf + calendarDay + lead) * DMKH - stockDT - HDD) / QC;
		
		if(pd.getRefProductId() != null) {
			Long KHTTparent = salePlanRepository.getQuantityByShopProduct(shopId, pd.getRefProductId(), firstMonthDay);
			if(KHTTparent == null) KHTTparent = 0l;
			if((KHTT > 0 && KHTTparent == 0) || (KHTT == 0 && KHTTparent > 0))
				YCDH = ((minSf + calendarDay + lead) * DMKH - stockDT - stockDTparent - HDD) / QC;
		}
		
		Long SLT = 0l;
		Integer moqValue = 0;
		Integer convfact2 = 0;
		
		Response<ShopParamDTO> SLTCheckResp = shopClient.getShopParamV1("PO_PARAM", "OVER_QUOTA", shopId);
		MOQShopProduct moq = moqShopProductRepository.getByShopIdAndProductId(shopId, productId);
		PalletShopProduct psd = palletShopProductRepository.getByShopIdAndProductId(shopId, productId);
		Response<ApParamDTO> ap = apparamClient.getApParamByCodeTypeV1("HSDHTD", "HSDHTD");
		PoAutoCoreShopProduct poc = poAutoCoreShopProductRepository.getByShopIdAndProductId(shopId, productId);
		if(moq != null) moqValue = moq.getMoqValue();
		if(psd != null) convfact2 = psd.getConvfact2();
		
		if(SLTCheckResp != null && SLTCheckResp.getData() != null) SLT = YCDH / QC;
		else {
			if(LKTT > KHTT * percentage) SLT = 0l;
			else if(LKTT + YCDH <= KHTT * percentage) SLT = YCDH;
			else if(LKTT + YCDH > KHTT * percentage) SLT = KHTT * percentage - LKTT;
			else if(YCDH > KHTT * maxSf) SLT = KHTT * maxSf / QC;
			if(SLT > 20) SLT = roundUpBy5(SLT);
		}
		
		if(moq != null) {
			if(YCDH <= 20) {
				if(moqValue > 0 && YCDH > 0 && YCDH / QC < moqValue) {
					if(YCDH / QC < 5 / 10 * moqValue) SLT = 0l;
					else SLT = Long.valueOf(moqValue);
				}
				else SLT = YCDH / QC;
			}
			else {
				if(moqValue > 0 && Math.ceil((double) YCDH / QC / 5) * 5 > 0 && Math.ceil((double) YCDH / QC / 5) * 5 < moqValue) {
					if(YCDH / QC < 5 / 10 * moqValue) SLT = 0l;
					else SLT = Long.valueOf(moqValue);
				}
				else SLT = (long) Math.ceil((double) YCDH / QC / 5) * 5;
			}
		}
		
		if(psd != null) {
			if(convfact2 == null || convfact2 == 0) return null;
				// return ERROR + ": SLT convfact2";
			if(psd.getPalletToValue() == null) {
				if(SLT / convfact2 < Math.floorDiv(SLT, convfact2) + psd.getPalletValue()) 
					SLT = Math.floorDiv(SLT, convfact2) * convfact2;
				else SLT = (long) (Math.ceil((double) SLT / convfact2) * convfact2);
			}
			else {
				if(SLT / convfact2 >= psd.getPalletValue() && SLT/convfact2 <= psd.getPalletToValue()) {
					if(SLT / convfact2 < Math.floorDiv(SLT, convfact2) + 5 / 10) 
						SLT = Math.floorDiv(SLT, convfact2) * convfact2;
					else SLT = (long) Math.ceil(SLT / convfact2) * convfact2;
				}
			}
		}
		
		if(poc != null && ap != null && ap.getData() != null) {
			SLT = SLT * ap.getData().getIntValue() * poc.getCoreValue();
			if(SLT > 20) SLT = roundUpBy5(SLT);
			if(SLT == 0) return null;
				// return ERROR + ": SLT core";
		}
		reqPO.setSoLuongThung(SLT);
		
		Price pr = priceRepository.getNewPriceOfProduct(productId);
		Long TT = (long) (SLT * QC * pr.getPrice());
		reqPO.setThanhTien(TT);
		
		Long TL = SLT * QC;
		reqPO.setTrongLuong(TL);
		
		String CB = "";
		String catCode = productInfoRepository.getProductInfoCodeById(pd.getCatId());
		
		if(DTTT > 14l) CB = "X";
		else if(("A".equals(catCode) || "B".equals(catCode)) && DTTT > 30l ) CB = "X";
		else if("D".equals(catCode) && DTTT > 10) CB = "X";
		else if(LKTT / countWorkingDay() < DMKH * 70 / 100 && now.getDayOfMonth() > 10) CB = "X";
		else if(DTTT > 4 * minSf && now.getDayOfMonth() > 10) CB = "QX";
		else if(YCDH == 0 && LKTT > KHTT) CB = "O";
		reqPO.setCanhBao(CB);
		
		return reqPO;
	}
	
	private Integer countWorkingDay() {
		LocalDate now = LocalDate.now();
		int sundayCount = (int) now.getDayOfMonth() / 7;
		Integer spareDay = now.getDayOfMonth() - sundayCount * 7;
		if(now.getDayOfWeek().getValue() + spareDay >= 7) sundayCount++;
		return now.getDayOfMonth() - sundayCount;
	}
	
	private Long roundUpBy5(Long number) {
	
		if(number % 5 == 0) return number;
		return number + 5 - number % 5;
	}
	
	private void handleSavePOAutoPOAutoDetail(ProductQuantityListDTO productQuantityListDTO, List<poSplitDTO> poSplitCat, List<poSplitDTO> poSplitSubCat, List<poSplitDTO> poSplitProd, Long shopId) {
		
		ProductQuantityListDTO result = new ProductQuantityListDTO();
		
		HashMap<Long, String> poNumberMap = new HashMap<Long, String>();
		
		if(productQuantityListDTO == null ||
				productQuantityListDTO.getProductQuantityList() == null ||
				productQuantityListDTO.getProductQuantityList().isEmpty()) return ;
		
		productQuantityListDTO.getProductQuantityList().forEach(pq -> {

			Product pd = productRepository.getByProductCode(pq.getProductCode());
			
			poSplitProd.forEach(pod -> {
				if(pod.getObjectId() != null && pod.getObjectId().equals(pd.getId())) {
					pq.setGroupId(pod.getId());
				}
			});
			
			if(pq.getGroupId() == null) {
				poSplitSubCat.forEach(pos -> {
					if(pos.getObjectId() != null && pos.getObjectId().equals(pd.getSubCatId())) {
						pq.setGroupId(pos.getId());
					}
				});
			}
			
			if(pq.getGroupId() == null) {
				poSplitCat.forEach(poc -> {
					if(poc.getObjectId() != null && poc.getObjectId().equals(pd.getCatId())) {
						pq.setGroupId(poc.getId());
					}
				});
			}
			
			if(pq.getGroupId() == null) {
				pq.setGroupId(-1l);
			}
			
			pq.setProductConv(pd.getConvFact());
			result.add(pq);
			
		});
		
		result.getProductQuantityList().forEach(n -> {
			if(poNumberMap.get(n.getGroupId()) == null) {
				String poNumber = getCurrentMaxPOAutoNumberId(shopId);
				if(StringUtils.stringIsNullOrEmpty(poNumber)) return;
				poNumberMap.put(n.getGroupId(), poNumber);
			}
			
			PoAuto po = poAutoRepository.getPoAutoBypoAutoNumber(poNumberMap.get(n.getGroupId()), shopId);
			
			if(po == null) {
				po = new PoAuto();
				po.setPoAutoNumber(poNumberMap.get(n.getGroupId()));
				po.setShopId(shopId);
				po.setStaffId(null);
				po.setPoAutoDate(LocalDateTime.now());
				po.setStatus(0);
				po.setCreateAt(LocalDateTime.now());
				po = poAutoRepository.save(po);
			}
			
			Product pd = productRepository.getByProductCode(n.getProductCode());
			Price pr = priceRepository.getNewPriceOfProduct(pd.getId());
			
			PoAutoDetail pod = new PoAutoDetail();
			pod.setPoAutoId(po.getId());
			pod.setProductId(pd.getId());
			pod.setPriceId(pr.getId());
			pod.setPrice(pr.getPrice().longValue());
			pod.setPoAutoDate(new Date());
			pod.setConvfact(Long.valueOf(n.getProductConv()));
			pod.setQuantity(n.getQuantity());
			pod.setAmount(pod.getQuantity() * pod.getConvfact() * pod.getPrice());
			pod.setCreatedAt(LocalDateTime.now());
			poAutoDetailRepository.save(pod);
		});
	}
	
	private String getCurrentMaxPOAutoNumberId(Long shopId) {
		
		try {
			Page<PoAutoDTO> a = poAutoRepository.findAllPo(shopId,PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
			return ("MTPO" + String.valueOf(Long.valueOf(a.getContent().get(0).getPoAutoNumber().substring(4)) + 1) );			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void poAutoExportXML(List<PoAuto> poAutoNumberList) {
		
		String ACCEPT_VALUE = "1";
		
		if(poAutoNumberList.isEmpty()) return;
        
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
        
        if (apParamDTOList == null) return;

        String uploadDestination = "/POCHGTSP/MTAUTOPO";
            
        FTPINFO ftpinfo = new FTPINFO(apParamDTOList);
        
        ConnectFTP connectFTP = new ConnectFTP(ftpinfo.getServer(), ftpinfo.getPortStr(), ftpinfo.getUserName(), ftpinfo.getPassword());

        for (PoAuto poAuto : poAutoNumberList) {
        	try {
        		ShopDTO shop = shopClient.getByIdV1(poAuto.getShopId()).getData();
        		if(shop == null) return;
        		String shopCode = shop.getShopCode();
        		if(!StringUtils.stringIsNullOrEmpty(shopCode)) {
        			String fileName = StringUtils.createXmlFileNameV2(shopCode);
        			InputStream inputStream = this.exportXmlFile(poAuto, shopCode, shop.getId());
        			if (inputStream != null) {
        				String finalLocate = uploadDestination + "/" + shopCode;
        				connectFTP.uploadFileV2(inputStream, fileName, finalLocate);
        			}
        		}
        	} catch (Exception ex) {
				LogFile.logToFile(appName, "schedule", LogLevel.ERROR, null, "Error parse sale order " + poAuto.getPoAutoNumber() + " to file - " + ex.getMessage());
				ex.printStackTrace();
			}
        }
        
        connectFTP.disconnectFTPServer();
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
	
    @Transactional(rollbackFor = Exception.class)
    private InputStream exportXmlFile(PoAuto poAuto, String shopCode, Long shopId) throws Exception {
        
    	if(poAuto == null) return null;
    	
    	String PAYMENT_TERM = "12 NET";
    	String SITE_ID = "KHOCHINH";
    	String VERSION = "1";
        
        PO po = new PO();
        List<NewPO> newPOList = new ArrayList<>();
        NewPO newPO = new NewPO();
        POHeader poHeader = new POHeader();

        poHeader.setDistCode(shopCode);
        poHeader.setPoNumber(poAuto.getPoAutoNumber());
        poHeader.setBillToLocation(poAuto.getBillToLocation());
        poHeader.setShipToLocation(poAuto.getShipToLocation());
        poHeader.setOrderDate(poAuto.getApproveDate());
        poHeader.setStatus(String.valueOf(poAuto.getStatus()));
        if(poAuto.getTotal() != null)
        	poHeader.setTotal(poAuto.getTotal().floatValue());
        poHeader.setPaymentTerm(PAYMENT_TERM);
        List<POHeader> poHeaderList = new ArrayList<>();
        poHeaderList.add(poHeader);
        
        PODetail poDetail = new PODetail();
        List<Line> lineList = new ArrayList<>();
        List<PoAutoDetail> poAutoDetailList = poAutoRepository.getPoAutoDetailById(poAuto.getId());
        
        poAutoDetailList.forEach(n -> {
        	
        	Integer Planqty = -1;
        	Calendar cal = Calendar.getInstance();
        	cal.set(Calendar.DAY_OF_MONTH, 1);
        	Date fromMonth = cal.getTime();
        	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        	Date toMonth = cal.getTime();
        	SalePlan sp = poAutoRepository.getSalePlanByShopIdProductIdMonth(shopId, n.getProductId(), fromMonth, toMonth);
        	if(sp != null) {
        		Planqty = sp.getQuantity();
        	}
            Line line = new Line();
            Product product = productRepository.getById(n.getProductId());
            line.setDistCode(shopCode);            	
            line.setPONumber(poAuto.getPoAutoNumber());
            line.setItemCode(product.getProductCode());
            line.setItemDescr(product.getProductName());
            line.setUom(product.getUom1());
            line.setSiteId(SITE_ID);
            if(n.getQuantity() != null)
            	line.setQuantity(n.getQuantity().intValue());
            line.setPrice(n.getPrice().doubleValue());
            if(n.getAmount() != null)
            	line.setLineTotal(n.getAmount().doubleValue());
            line.setRequestDate(poAuto.getPoAutoDate());
            line.setVersion(VERSION);
            line.setPlanqty(Planqty);
            lineList.add(line);
        });
        
        poDetail.setLstLine(lineList);
        List<PODetail> poDetailList = new ArrayList<>();
        poDetailList.add(poDetail);
        newPO.setPoHeader(poHeaderList);
        newPO.setPoDetailList(poDetailList);
        newPOList.add(newPO);
        po.setNewPOList(newPOList);

        xstream.processAnnotations(classes);
        xstream.toXMLFile(po);
        String xml = xstream.toXML(po);
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        
    }
	
    @Getter
    @Setter
    @NoArgsConstructor
    private class FTPINFO {
       private String server;
       private String portStr;
       private String userName;
       private String password;
        public FTPINFO(List<ApParamDTO> apParamDTOList) {
            if(apParamDTOList != null){
                for(ApParamDTO app : apParamDTOList){
                    if(app.getApParamCode() == null || "FTP_SERVER".equalsIgnoreCase(app.getApParamCode().trim())) server = app.getValue().trim();
                    if(app.getApParamCode() == null || "FTP_USER".equalsIgnoreCase(app.getApParamCode().trim())) userName = app.getValue().trim();
                    if(app.getApParamCode() == null || "FTP_PASS".equalsIgnoreCase(app.getApParamCode().trim())) password = app.getValue().trim();
                    if(app.getApParamCode() == null || "FTP_PORT".equalsIgnoreCase(app.getApParamCode().trim())) portStr = app.getValue().trim();
                }
            }
        }
    }

}
