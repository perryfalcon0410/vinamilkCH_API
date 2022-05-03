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
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.PoAuto;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SalePlan;
import vn.viettel.sale.entities.PoAutoDetail;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.repository.PalletShopProductRepository;
import vn.viettel.sale.repository.PoAutoDetailRepository;
import vn.viettel.sale.repository.PoAutoGroupRepository;
import vn.viettel.sale.repository.PoAutoRepository;
import vn.viettel.sale.repository.PoTransDetailRepository;
import vn.viettel.sale.repository.PriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.SaleDayRepository;
import vn.viettel.sale.repository.SalePlanRepository;
import vn.viettel.sale.repository.StockAdjustmentTransDetailRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.PoAutoService;
import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;
import vn.viettel.sale.service.dto.ProductQuantityListDTO;
import vn.viettel.sale.service.dto.ProductStockDTO;
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
    ApparamClient apparamClient;
    
    @Autowired
    ShopClient shopClient;
    
    @Autowired
    ReportStockClient reportStockClient;
    
    @Value("${spring.application.name}")
    public String appName;
    
    XStreamTranslator xstream = XStreamTranslator.getInstance();
    
    private Class<?>[] classes = new Class[] { PO.class, NewPO.class, POHeader.class, PODetail.class, Line.class};
    
    String ERROR = "Có lỗi trong quá trình xử lý.";
    String SUCCESS = "Thành công.";

	@Override
	public Page<PoAutoDTO> getAllPoAuto(Long shopId, int page) {

		Pageable pageable = PageRequest.of(page, 5, Sort.by("poAutoNumber"));
		
		LocalDate today = LocalDate.now();
		
		String test1 = getOfferPoAuto(2742l, 8321l);
		
		// long khi cong lai ma null thi gay ra nullException
		Long test2 = poAutoRepository.getImportQuantity1(2742l, null, null) + poAutoRepository.getImportQuantity2(2742l, null);
		
		// long khi cong lai ma null thi gay ra nullException
		Long test3 = poAutoRepository.getExportQuantity1(2742l, null, null) + poAutoRepository.getExportQuantity2(2742l, null);
		
		Long test4 = poAutoRepository.getExportQuantity3(2742l, null);
		
		// xet null or = 0 thi loai san pham do ra
		Long KeHoachTieuThuThang = salePlanRepository.getQuantityByShopProduct(2742l, 8321l, null);
		
		// check null, 0
		Long DinhMucKeHoach = KeHoachTieuThuThang/saleDayRepository.getDayMonthByShopId(shopId, null);
		
		// check null, 0
		Long NgayDuTruKH = (stockTotalRepository.getStockTotalByShopProduct(2742l, 3471l, 8321l)/DinhMucKeHoach);
		
		return poAutoRepository.findAllPo(shopId, pageable);
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
		if (poAuto != null) {
			poAutoRepository.getPoAutoDetailById(poAuto.getId()).forEach((n) -> {
				poAutoDetailProductList.add(convertPoAutoDetail(n));
			});			
		}
		
		return poAutoDetailProductList;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String approvePoAuto(List<String> poAutoNumberList, Long shopId) {
		
		Integer UNAPPROVE_STATUS = 0;
		
		if(poAutoNumberList.isEmpty()) return ERROR;
		
		try {
			List<PoAuto> poAutoSucList = new ArrayList<>();
			poAutoNumberList.forEach(n -> {
				PoAuto temp = poAutoRepository.getPoAutoBypoAutoNumber(n, shopId);
				if(UNAPPROVE_STATUS.equals(temp.getStatus())) {
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
		
		Integer CANCEL_STATUS = 2;
		
		if(poAutoNumberList.isEmpty()) return ERROR;
		
		poAutoNumberList.forEach(n -> {
			PoAuto temp = poAutoRepository.getPoAutoBypoAutoNumber(n, shopId);
			if(!CANCEL_STATUS.equals(temp.getStatus())) {
				poAutoRepository.cancelPo(n, LocalDateTime.now(), shopId);
			}
		});
		
		return SUCCESS;
	}
	
	@Override
	public Page<ProductStockDTO> getProductByPage(Pageable pageable, Long shopid, String keyword) {

		List<Tuple> productList = poAutoRepository.getProductByPage(shopid, keyword);
		
		if(productList == null || productList.size() < 1) return null;
		
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
	
	public String getOfferPoAuto(Long shopId, Long productId) {
		
		try {
			LocalDate lastMonthDay = LocalDate.now();
			lastMonthDay = lastMonthDay.withDayOfMonth(1).minusDays(1);
			Response<Long> test = reportStockClient.getStockAggregated(shopId, productId, lastMonthDay); 
			System.out.println("aaa");
		}
		catch(Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	private void handleSavePOAutoPOAutoDetail(ProductQuantityListDTO productQuantityListDTO, List<poSplitDTO> poSplitCat, List<poSplitDTO> poSplitSubCat, List<poSplitDTO> poSplitProd, Long shopId) {
		
		ProductQuantityListDTO result = new ProductQuantityListDTO();
		
		HashMap<Integer, String> poNumberMap = new HashMap<Integer, String>();
		
		if(productQuantityListDTO == null ||
				productQuantityListDTO.getProductQuantityList() == null ||
				productQuantityListDTO.getProductQuantityList().size() == 0) return ;
		
		productQuantityListDTO.getProductQuantityList().forEach(n -> {

			Product pd = productRepository.getByProductCode(n.getProductCode());
			
			poSplitProd.forEach(m -> {
				if(m.getObjectId() == pd.getId()) {
					n.setGroupId(m.getId().intValue());
				}
			});
			
			if(n.getGroupId() == null) {
				poSplitSubCat.forEach(m -> {
					if(m.getObjectId() == pd.getSubCatId()) {
						n.setGroupId(m.getId().intValue());
					}
				});
			}
			
			if(n.getGroupId() == null) {
				poSplitCat.forEach(m -> {
					if(m.getObjectId() == pd.getCatId()) {
						n.setGroupId(m.getId().intValue());
					}
				});
			}
			
			if(n.getGroupId() == null) {
				n.setGroupId(-1);
			}
			
			n.setProductConv(pd.getConvFact());
			result.add(n);
			
		});
		
		result.getProductQuantityList().forEach(n -> {
			if(poNumberMap.get(n.getGroupId()) == null) {
				poNumberMap.put(n.getGroupId(), getCurrentMaxPOAutoNumberId());
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
	
	private String getCurrentMaxPOAutoNumberId() {
		
		PoAuto a = poAutoRepository.getNewestPoAutoNumber().get(0);
		return ("MTPO" + String.valueOf(Long.valueOf(a.getPoAutoNumber().substring(4)) + 1) );
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void poAutoExportXML(List<PoAuto> poAutoNumberList) {
		
		String ACCEPT_VALUE = "1";
		
		if(poAutoNumberList.isEmpty()) return;
        
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
        
        if (apParamDTOList == null) return;
        
        if (apParamDTOList.get(0).getValue().equals(ACCEPT_VALUE)) {
        	// Do the download thing
        	
        }

        String uploadDestination = "/POCHGTSP/MTAUTOPO";
            
        FTPINFO ftpinfo = new FTPINFO(apParamDTOList);
        
        ConnectFTP connectFTP = new ConnectFTP(ftpinfo.getServer(), ftpinfo.getPortStr(), ftpinfo.getUserName(), ftpinfo.getPassword());

        for (PoAuto poAuto : poAutoNumberList) {
        	try {
        		if(shopClient.getByIdV1(poAuto.getShopId()).getData() == null) return;
        		String shopCode = shopClient.getByIdV1(poAuto.getShopId()).getData().getShopCode();
        		if(!shopClient.getByIdV1(poAuto.getShopId()).getData().getShopCode().isEmpty()) {        			
        			String fileName = StringUtils.createXmlFileNameV2(shopCode);
        			InputStream inputStream = this.exportXmlFile(poAuto, shopCode);
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
    private InputStream exportXmlFile(PoAuto poAuto, String shopCode) throws Exception {
        
    	if(poAuto == null) return null;
    	
    	String PaymentTerm = "12 NET";
    	String SiteID = "KHOCHINH";
    	String Version = "1";
        
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
        poHeader.setTotal(poAuto.getTotal().floatValue());
        poHeader.setPaymentTerm(PaymentTerm);
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
        	SalePlan sp = poAutoRepository.getSalePlanByShopIdProductIdMonth(shopCode, n.getProductId(), fromMonth, toMonth);
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
            line.setSiteId(SiteID);
            line.setQuantity(n.getQuantity().intValue());
            line.setPrice(n.getPrice().doubleValue());
            line.setLineTotal(n.getAmount().doubleValue());
            line.setRequestDate(poAuto.getPoAutoDate());
            line.setVersion(Version);
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
