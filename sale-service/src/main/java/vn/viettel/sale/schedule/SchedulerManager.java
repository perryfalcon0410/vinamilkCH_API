package vn.viettel.sale.schedule;

//import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.repository.OnlineOrderRepository;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.util.ConnectFTP;
import vn.viettel.sale.xml.Header;
import vn.viettel.sale.xml.NewDataSet;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchedulerManager{

//	@Autowired
//	ApparamClient apparamClient;

	@Autowired
	OnlineOrderService onlineOrderService;

//	@Autowired
//	OnlineOrderRepository onlineOrderRepository;
//
//	@Autowired
//	ShopClient shopClient;

//	@Autowired
//	private SecurityContexHolder securityContexHolder;
//
//	public Long getShopId() {
//		return securityContexHolder.getContext().getShopId();
//	}

//	@Scheduled(cron = "* */1 * * * *")
	@SchedulerLock(name = "getOnlineOrder")
	public void getOnlineOrder() throws InterruptedException {
		onlineOrderService.getOnlineOrderSchedule();
	}

//	@Scheduled(cron = "* */10 * * * *")
	@SchedulerLock(name = "uploadOnlineOrder")
	public void uploadOnlineOrder() throws InterruptedException {
		onlineOrderService.uploadOnlineOrderSchedule();
	}



//	@Scheduled(cron = "* */10 * * * *")
//	@Transactional(propagation=Propagation.REQUIRES_NEW)
//	@SchedulerLock(name = "getOnlineOrder")
//	public void getOnlineOrder() throws InterruptedException {
//		List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
//		String readPath = "/home/kch/pos/neworder", backupPath = "/home/kch/pos/backup", newOrder = "_VES_", cancelOrder = "_CANORDERPOS_"
//				, destinationMessage = "/home/kch/pos/ordermessage", failName = "VES_ORDERMESSAGE_";
//		if(apParamDTOList != null){
//			for(ApParamDTO app : apParamDTOList){
//				if(app.getApParamCode() == null || "FTP_ORDER".equalsIgnoreCase(app.getApParamCode().trim())) readPath = app.getValue().trim();
//				if(app.getApParamCode() == null || "FTP_ONLINE_BACKUP".equalsIgnoreCase(app.getApParamCode().trim())) backupPath = app.getValue().trim();
//				if(app.getApParamCode() == null || "FTP_FILE_NEW".equalsIgnoreCase(app.getApParamCode().trim())) newOrder = app.getValue().trim();
//				if(app.getApParamCode() == null || "FTP_FILE_CANCEL".equalsIgnoreCase(app.getApParamCode().trim())) cancelOrder = app.getValue().trim();
//				if (app.getApParamCode() == null || "FTP_MESSAGE".equalsIgnoreCase(app.getApParamCode().trim()))
//					destinationMessage = app.getValue().trim();
//				if (app.getApParamCode() == null || "FTP_FILE_FAIL".equalsIgnoreCase(app.getApParamCode().trim()))
//					failName = app.getValue().trim();
//			}
//		}
//		ConnectFTP connectFTP = connectFTP(apParamDTOList);
//		//read new order
//		HashMap<String, InputStream> newOrders = connectFTP.getFiles(readPath, newOrder);
//		if(newOrders != null){
//			for (Map.Entry<String, InputStream> entry : newOrders.entrySet()){
//				try {
//					onlineOrderService.syncXmlOnlineOrder(entry.getValue());
//					connectFTP.moveFile(readPath, backupPath, entry.getKey());
//				}catch (Exception ex) {
//					LogFile.logToFile("", "", LogLevel.ERROR, null, "Error while read file " + entry.getKey() + " - " + ex.getMessage());
//				}
//			}
//		}
//
//		//read cancel order
//		HashMap<String, InputStream> cancelOrders = connectFTP.getFiles(readPath, cancelOrder);
//		if(cancelOrders != null){
//			for (Map.Entry<String, InputStream> entry : cancelOrders.entrySet()){
//				try {
//					onlineOrderService.syncXmlToCancelOnlineOrder(entry.getValue());
//					connectFTP.moveFile(readPath, backupPath, entry.getKey());
//				}catch (Exception ex) {
//					LogFile.logToFile("", "", LogLevel.ERROR, null, "Error while read file " + entry.getKey() + " - " + ex.getMessage());
//				}
//			}
//		}
//		connectFTP.disconnectServer();
//	}
//
//	@Scheduled(cron = "* */1 * * * *")
//	@Transactional(rollbackFor = Exception.class)
//	@SchedulerLock(name = "uploadOnlineOrder")
//	public void uploadOnlineOrder() throws InterruptedException {
//		List<Long> shops = onlineOrderRepository.findALLShopId();
//		if(shops.size() > 0) {
//
//			//set ap param value
//			List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
//
//			String uploadDestination = "/home/kch/pos/downorderpos", successName = "ORDERPOS_";
//			if (apParamDTOList != null) {
//				for (ApParamDTO app : apParamDTOList) {
//					if (app.getApParamCode() == null || "FTP_UPLOAD".equalsIgnoreCase(app.getApParamCode().trim()))
//						uploadDestination = app.getValue().trim();
//					if (app.getApParamCode() == null || "FTP_FILE_SUC".equalsIgnoreCase(app.getApParamCode().trim()))
//						successName = app.getValue().trim();
//				}
//			}
//			ConnectFTP connectFTP = connectFTP(apParamDTOList);
//			for (Long shopId : shops) {
//				List<OnlineOrder> onlineOrders = onlineOrderRepository.findOnlineOrderExportXml(shopId);
//				ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
//				if (onlineOrders != null && !onlineOrders.isEmpty()) {
//						try {
//							String fileName = successName + StringUtils.createXmlFileName(shopDTO.getShopCode());
//							InputStream inputStream = onlineOrderService.exportXmlFile(onlineOrders);
//							if (inputStream != null)
//								connectFTP.uploadFile(inputStream, fileName, uploadDestination);
//						} catch (Exception ex) {
//							LogFile.logToFile("", "", LogLevel.ERROR, null, "Error parse sale order " + shopDTO.getShopCode() + " to file - " + ex.getMessage());
//						}
//				}
//			}
//			connectFTP.disconnectServer();
//		}
//	}
//
//	private ConnectFTP connectFTP(List<ApParamDTO> apParamDTOList){
//		String server = "192.168.100.112", portStr = null, userName = "kch", password = "Viett3l$Pr0ject";
//		if(apParamDTOList != null){
//			for(ApParamDTO app : apParamDTOList){
//				if(app.getApParamCode() == null || "FTP_SERVER".equalsIgnoreCase(app.getApParamCode().trim())) server = app.getValue().trim();
//				if(app.getApParamCode() == null || "FTP_USER".equalsIgnoreCase(app.getApParamCode().trim())) userName = app.getValue().trim();
//				if(app.getApParamCode() == null || "FTP_PASS".equalsIgnoreCase(app.getApParamCode().trim())) password = app.getValue().trim();
//				if(app.getApParamCode() == null || "FTP_PORT".equalsIgnoreCase(app.getApParamCode().trim())) portStr = app.getValue().trim();
//			}
//		}
//		return new ConnectFTP(server, portStr, userName, password);
//	}
}
