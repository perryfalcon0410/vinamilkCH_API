package vn.viettel.sale.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.PoAuto;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.SalePlan;
import vn.viettel.sale.entities.PoAutoDetail;
import vn.viettel.sale.repository.PoAutoRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.PoAutoService;
import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;
import vn.viettel.sale.service.dto.ProductStockDTO;
import vn.viettel.sale.service.feign.ApparamClient;
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
	ProductRepository productRepository;
	
    @Autowired
    ApparamClient apparamClient;
    
    @Autowired
    ShopClient shopClient;
    
    @Value("${spring.application.name}")
    public String appName;
    
    XStreamTranslator xstream = XStreamTranslator.getInstance();
    
    private Class<?>[] classes = new Class[] { PO.class, NewPO.class, POHeader.class, PODetail.class, Line.class};
    
    int SUCCESS = 1;
    int FAIL = 0;

	@Override
	public List<PoAutoDTO> getAllPoAuto(Long shopId, int page) {

		List<PoAutoDTO> poAutoDTOList = new ArrayList<PoAutoDTO>();
		Pageable pageable = PageRequest.of(page, 5, Sort.by("poAutoNumber"));
		
		poAutoRepository.findAllPo(shopId, pageable).forEach((n) -> {
			poAutoDTOList.add(convertToDTO(n));
		});
		
		return poAutoDTOList;
	}

	@Override
	public List<PoAutoDTO> getSearchPoAuto(String poAutoNumber, String poGroupCode, LocalDateTime fromCreateDate, LocalDateTime toCreateDate, LocalDateTime fromApproveDate, LocalDateTime toApproveDate, int poStatus, Long shopId, int page) {
		
		List<PoAutoDTO> poAutoDTOList = new ArrayList<PoAutoDTO>();
		Pageable pageable = PageRequest.of(page, 5, Sort.by("poAutoNumber"));
		
		poAutoRepository.searchPoList(poAutoNumber, poGroupCode, fromCreateDate, toCreateDate, fromApproveDate, toApproveDate, poStatus, shopId, pageable).forEach((n) -> {
			poAutoDTOList.add(convertToDTO(n));
		});
		return poAutoDTOList;
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
	public int approvePoAuto(List<String> poAutoNumberList, Long shopId) {		
		
		if(poAutoNumberList.isEmpty()) return FAIL;
		
		List<PoAuto> poAutoSucList = new ArrayList<>();
		poAutoNumberList.forEach(n -> {
			PoAuto temp = poAutoRepository.getPoAutoBypoAutoNumber(n, shopId);
			if(temp.getStatus() == 0) {
				poAutoRepository.approvePo(n, LocalDateTime.now(), shopId);
				poAutoSucList.add(temp);
			}
		});
		
		if(!poAutoSucList.isEmpty()) poAutoExportXML(poAutoSucList);
		
		return SUCCESS;
	}
	
	@Override
    @Transactional(rollbackFor = Exception.class)
	public int cancelPoAuto(List<String> poAutoNumberList, Long shopId) {
		
		if(poAutoNumberList.isEmpty()) return FAIL;
		
		poAutoNumberList.forEach(n -> {
			PoAuto temp = poAutoRepository.getPoAutoBypoAutoNumber(n, shopId);
			if(temp.getStatus() != 2) {
				poAutoRepository.cancelPo(n, LocalDateTime.now(), shopId);				
			}
		});
		
		return SUCCESS;
	}
	
	@Override
	public Page<ProductStockDTO> getProductByPage(int page) {
		
		Pageable pageable = PageRequest.of(page, 5);
		Page<ProductStockDTO> productList = productRepository.getProductByPage(pageable);
		
		return productList;
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
