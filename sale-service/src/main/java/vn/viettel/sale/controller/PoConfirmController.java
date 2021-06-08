package vn.viettel.sale.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.PoConfirmService;
import vn.viettel.sale.xml.NewDataSet;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
public class PoConfirmController extends BaseController {
    private final String root = "/sales/po-confirm";

    @Autowired
    PoConfirmService poConfirmService;

    @ApiOperation(value = "Api dùng để đọc file xml của đơn bán hàng")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/xml/po"})
    public Response<NewDataSet> syncXmlPo(HttpServletRequest httpRequest,
                                  @RequestParam(name = "file") MultipartFile file
                                  ) throws IOException {
        NewDataSet newDataSet = poConfirmService.syncXmlPo(file);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.SYNCHRONIZATION_XML_PO_SUCCESS);
        return new Response<NewDataSet>().withData(newDataSet);
    }

}
