package vn.viettel.sale.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.repository.OnlineOrderRepository;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.feign.ApparamClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.util.ConnectFTP;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchedulerManager{

	@Autowired
	ApparamClient apparamClient;

	@Autowired
	OnlineOrderService onlineOrderService;

	@Autowired
	OnlineOrderRepository onlineOrderRepository;

	@Autowired
	ShopClient shopClient;

	@Autowired
	private SecurityContexHolder securityContexHolder;

	public Long getShopId() {
		return securityContexHolder.getContext().getShopId();
	}

//	@Scheduled(cron = "* */1 * * * *")
	public void getOnlineOrder() throws InterruptedException {
		List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
		String readPath = "/pos/neworder", backupPath = "/pos/backup", newOrder = "_VES_", cancelOrder = "_CANORDERPOS_"
				, destinationMessage = "/pos/ordermessage", failName = "VES_ORDERMESSAGE_";
		if(apParamDTOList != null){
			for(ApParamDTO app : apParamDTOList){
				if(app.getApParamCode() == null || "FTP_ORDER".equalsIgnoreCase(app.getApParamCode().trim())) readPath = app.getValue().trim();
				if(app.getApParamCode() == null || "FTP_ONLINE_BACKUP".equalsIgnoreCase(app.getApParamCode().trim())) backupPath = app.getValue().trim();
				if(app.getApParamCode() == null || "FTP_FILE_NEW".equalsIgnoreCase(app.getApParamCode().trim())) newOrder = app.getValue().trim();
				if(app.getApParamCode() == null || "FTP_FILE_CANCEL".equalsIgnoreCase(app.getApParamCode().trim())) cancelOrder = app.getValue().trim();
				if (app.getApParamCode() == null || "FTP_MESSAGE".equalsIgnoreCase(app.getApParamCode().trim()))
					destinationMessage = app.getValue().trim();
				if (app.getApParamCode() == null || "FTP_FILE_FAIL".equalsIgnoreCase(app.getApParamCode().trim()))
					failName = app.getValue().trim();
			}
		}
		ConnectFTP connectFTP = connectFTP(apParamDTOList);
		//read new order
		HashMap<String, InputStream> newOrders = connectFTP.getFiles(readPath, newOrder);
		if(newOrders != null){
			for (Map.Entry<String, InputStream> entry : newOrders.entrySet()){
				try {
					onlineOrderService.syncXmlOnlineOrder(entry.getValue());
					connectFTP.moveFile(readPath, backupPath, entry.getKey());
				}catch (Exception ex) {
					LogFile.logToFile("", "", LogLevel.ERROR, null, "Error while read file " + entry.getKey() + " - " + ex.getMessage());
				}
			}
		}

		//read cancel order
		HashMap<String, InputStream> cancelOrders = connectFTP.getFiles(readPath, cancelOrder);
		if(cancelOrders != null){
			for (Map.Entry<String, InputStream> entry : cancelOrders.entrySet()){
				try {
					onlineOrderService.syncXmlToCancelOnlineOrder(entry.getValue());
					connectFTP.moveFile(readPath, backupPath, entry.getKey());
				}catch (Exception ex) {
					LogFile.logToFile("", "", LogLevel.ERROR, null, "Error while read file " + entry.getKey() + " - " + ex.getMessage());
				}
			}
		}
		connectFTP.disconnectServer();
	}

//	@Scheduled(cron = "* */1 * * * *")
	public void uploadOnlineOrder() throws InterruptedException {
		ShopDTO shopDTO = shopClient.getByIdV1(1L).getData();
		List<OnlineOrder> onlineOrders = onlineOrderRepository.findOnlineOrderExportXml(shopDTO.getId());

		if(onlineOrders != null && !onlineOrders.isEmpty()) {
			List<ApParamDTO> apParamDTOList = apparamClient.getApParamByTypeV1("FTP").getData();
			String uploadDestination = "/pos/downorderpos", successName = "ORDERPOS_";
			if (apParamDTOList != null) {
				for (ApParamDTO app : apParamDTOList) {
					if (app.getApParamCode() == null || "FTP_UPLOAD".equalsIgnoreCase(app.getApParamCode().trim()))
						uploadDestination = app.getValue().trim();
					if (app.getApParamCode() == null || "FTP_FILE_SUC".equalsIgnoreCase(app.getApParamCode().trim()))
						successName = app.getValue().trim();
				}
			}
			ConnectFTP connectFTP = connectFTP(apParamDTOList);
			for(OnlineOrder onlineOrder : onlineOrders){
				try {
					String fileName = successName + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss")) + "_" + shopDTO.getShopCode();
					InputStream inputStream = onlineOrderService.exportXmlFile(onlineOrder);
					if(inputStream != null)
						connectFTP.uploadFile(inputStream, fileName, uploadDestination);
				}catch (Exception ex) {
					LogFile.logToFile("", "", LogLevel.ERROR, null, "Error parse sale order " + onlineOrder.getOrderNumber() + " to file - " + ex.getMessage());
				}
			}
			connectFTP.disconnectServer();
		}
	}

	private ConnectFTP connectFTP(List<ApParamDTO> apParamDTOList){
		String server = null, portStr = null, userName = null, password = null;
		if(apParamDTOList != null){
			for(ApParamDTO app : apParamDTOList){
				if(app.getApParamCode() == null || "FTP_SERVER".equalsIgnoreCase(app.getApParamCode().trim())) server = app.getValue().trim();
				if(app.getApParamCode() == null || "FTP_USER".equalsIgnoreCase(app.getApParamCode().trim())) userName = app.getValue().trim();
				if(app.getApParamCode() == null || "FTP_PASS".equalsIgnoreCase(app.getApParamCode().trim())) password = app.getValue().trim();
				if(app.getApParamCode() == null || "FTP_PORT".equalsIgnoreCase(app.getApParamCode().trim())) portStr = app.getValue().trim();
			}
		}
		return new ConnectFTP(server, portStr, userName, password);
	}
}
