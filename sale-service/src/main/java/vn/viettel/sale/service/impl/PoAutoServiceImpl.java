package vn.viettel.sale.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.PoAuto;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.PoAutoDetail;
import vn.viettel.sale.repository.PoAutoRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.PoAutoService;
import vn.viettel.sale.service.dto.PoAutoDTO;
import vn.viettel.sale.service.dto.PoAutoDetailProduct;
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

	@Override
	public List<PoAutoDTO> getAllPoAuto(Long shopId) {

		List<PoAutoDTO> poAutoDTOList = new ArrayList<PoAutoDTO>();
		
		poAutoRepository.findAll(Sort.by(Sort.Direction.ASC, "poAutoNumber")).forEach((n) -> {
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
	public List<PoAutoDetailProduct> getPoAutoDetailProduct(String poAutoNumber) {
		
		List<PoAutoDetailProduct> poAutoDetailProductList = new ArrayList<PoAutoDetailProduct>(); 
		
		PoAuto poAuto = poAutoRepository.getPoAutoBypoAutoNumber(poAutoNumber);
		
		poAutoRepository.getPoAutoDetailById(poAuto.getId()).forEach((n) -> {
			poAutoDetailProductList.add(convertPoAutoDetail(n));
		});
		
		return poAutoDetailProductList;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int approvePoAuto(List<String> poAutoNumberList) {
		
		List<PoAuto> poAutoSucList = new ArrayList<>();
		poAutoNumberList.forEach(n -> {
			PoAuto temp = poAutoRepository.getPoAutoBypoAutoNumber(n);
			//if(temp.getStatus() == 0) {
				LocalDateTime date = LocalDateTime.now();
				poAutoRepository.approvePo(n, date);
				poAutoSucList.add(temp);
			//}
		});
		
		PoAutoExportXML(poAutoSucList);
		
		return 1;
	}
	
	@Override
    @Transactional(rollbackFor = Exception.class)
	public int cancelPoAuto(List<String> poAutoNumberList) {
		
		poAutoNumberList.forEach(n -> {
			Date date = new Date();
			poAutoRepository.cancelPo(n,DateUtils.convertFromDate(date));
		});
		
		return 1;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void PoAutoExportXML(List<PoAuto> poAutoNumberList) {
		
		if(poAutoNumberList.size() <= 0) return;
        
        List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
        
        // if (apParamDTOList == null || !apParamDTOList.get(0).getValue().equals("1")) return;

        String uploadDestination = "/POCHGTSP/MTAUTOPO";
            
        FTPINFO ftpinfo = new FTPINFO(apParamDTOList);
        ConnectFTP connectFTP = new ConnectFTP(ftpinfo.getServer(), ftpinfo.getPortStr(), ftpinfo.getUserName(), ftpinfo.getPassword());

        for (PoAuto poAuto : poAutoNumberList) {
        	try {
        		System.out.println(poAuto.getPoAutoNumber());
        		String fileName = StringUtils.createXmlFileName(poAuto.getPoAutoNumber());
				InputStream inputStream = this.exportXmlFile(poAuto);
				if (inputStream != null) {
					System.out.println(poAuto.getPoAutoNumber());
					connectFTP.uploadFile(inputStream, fileName, uploadDestination);
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
    private InputStream exportXmlFile(PoAuto poAuto) throws Exception {
        
    	if(poAuto == null) return null;
        
        PO po = new PO();
        List<NewPO> newPOList = new ArrayList<>();
        NewPO newPO = new NewPO();
        POHeader poHeader = new POHeader();
        try {
        if(shopClient.getByIdV1(poAuto.getShopId()).getData() != null )
        	poHeader.setDistCode(shopClient.getByIdV1(poAuto.getShopId()).getData().getShopCode());
        else poHeader.setDistCode("No shop found");
        poHeader.setPoNumber(poAuto.getPoAutoNumber());
        poHeader.setBillToLocation(poAuto.getBillToLocation());
        poHeader.setShipToLocation(poAuto.getShipToLocation());
        poHeader.setOrderDate(poAuto.getApproveDate());
        poHeader.setStatus(String.valueOf(poAuto.getStatus()));
        poHeader.setTotal(poAuto.getTotal().floatValue());
        poHeader.setPaymentTerm("12 NET");
        List<POHeader> poHeaderList = new ArrayList<>();
        poHeaderList.add(poHeader);
        
        PODetail poDetail = new PODetail();
        List<Line> lineList = new ArrayList<>();
        System.out.println(poAuto.getId());
        List<PoAutoDetail> poAutoDetailList = poAutoRepository.getPoAutoDetailById(poAuto.getId());
        
        poAutoDetailList.forEach(n -> {
        	
            Line line = new Line();
            Product product = productRepository.getById(n.getProductId());
            if(shopClient.getByIdV1(poAuto.getShopId()).getData() != null )
            	line.setDistCode(shopClient.getByIdV1(poAuto.getShopId()).getData().getShopCode());
            else line.setDistCode("No shop found");
            line.setPONumber(poAuto.getPoAutoNumber());
            line.setItemCode(product.getProductCode());
            line.setItemDescr(product.getProductName());
            line.setUom(product.getUom1());
            line.setSiteId("KHOCHINH");
            line.setQuantity(n.getQuantity().intValue());
            line.setPrice(n.getPrice().doubleValue());
            line.setLineTotal(n.getAmount().doubleValue());
            line.setRequestDate(poAuto.getPoAutoDate());
            line.setVersion("1");
            line.setPlanqty("12267");
            lineList.add(line);
        });
        
        poDetail.setLstLine(lineList);
        List<PODetail> poDetailList = new ArrayList<>();
        poDetailList.add(poDetail);
        newPO.setPoHeader(poHeaderList);
        newPO.setPoDetailList(poDetailList);
        newPOList.add(newPO);
        po.setNewPOList(newPOList);
        } catch (Exception ex) {
        	ex.printStackTrace();        
        }
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
