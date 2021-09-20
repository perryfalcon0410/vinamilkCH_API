package vn.viettel.sale.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.service.PoConfirmService;
import vn.viettel.sale.service.dto.PoConfirmXmlDTO;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PoConfirmController extends BaseController {
    private final String root = "/sales/po-confirm";

    @Autowired
    PoConfirmService poConfirmService;
    @Autowired
    JMSSender jmsSender;

    @ApiOperation(value = "Api dùng để đồng bộ lại danh sách Po Confirm lên db của đơn bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 201, message = "Failed")}
    )
    @GetMapping(value = { V1 + root + "/xml"})
    public Response<Boolean> updatePoCofirm(HttpServletRequest httpRequest){
        PoConfirmXmlDTO result = poConfirmService.updatePoCofirm(this.getShopId(httpRequest));
        sendSynRequest(JMSType.po_confirm, result.getPoConfirmIds());
        Response<Boolean> response = new Response<>();
        response.setStatusValue(result.getStatus());
        if(result.getResult() == true)
        {
            response.setStatusCode(200);
            LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.UPDATE_PO_CONFIRM_SUCCESS);
        }else{
            response.setStatusCode(201);
            LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.UPDATE_PO_CONFIRM_FAILED);
        }
        return response.withData(result.getResult());
    }
    
	private void sendSynRequest(String type, List<Long> lstIds) {
		try {
			if(lstIds != null && !lstIds.isEmpty()) {
				jmsSender.sendMessage(type, lstIds);
			}
		} catch (Exception ex) {
			LogFile.logToFile("PoConfirmController.sendSynRequest", type, LogLevel.ERROR, null, "has error when encode data " + ex.getMessage());
		}
	}

}
