package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.common.entities.PrinterConfig;
import vn.viettel.common.messaging.PrinterConfigRequest;
import vn.viettel.common.service.ApParamService;
import vn.viettel.common.service.PrinterConfigService;
import vn.viettel.common.service.dto.PrinterConfigDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.Response;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class PrinterConfigController extends BaseController {

    @Autowired
    PrinterConfigService service;

    private final String root = "/commons/printer-client";

    @ApiOperation(value = "Tạo cấu hình máy in")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PostMapping(value = { V1 + root})
    public Response<PrinterConfigDTO> create(HttpServletRequest httpRequest, @Valid @RequestBody PrinterConfigRequest request) {
        if( request.getClientIp() == null || request.getClientIp().isEmpty()) request.setClientIp(this.getClientIp(httpRequest));
        PrinterConfigDTO config = service.create(request);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.CREATE_PRINTER_CONFIG_SUCCESS);
        return new Response<PrinterConfigDTO>().withData(config);
    }

    @ApiOperation(value = "Cập nhật cấu hình máy in")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @PutMapping(value = { V1 + root + "/{id}"})
    public Response<PrinterConfigDTO> update(HttpServletRequest httpRequest, @PathVariable Long id,  @Valid @RequestBody PrinterConfigRequest request) {
        PrinterConfigDTO config = service.update(id, request);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.UPDATE_PRINTER_CONFIG_SUCCESS);
        return new Response<PrinterConfigDTO>().withData(config);
    }

    @ApiOperation(value = "Lấy cấu hình máy in")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request")}
    )
    @GetMapping(value = { V1 + root + "/{clientId}"})
    public Response<PrinterConfigDTO> update(HttpServletRequest httpRequest, @PathVariable String clientId) {
        getClientIp(httpRequest);
        PrinterConfigDTO config = service.getPrinter(clientId);
        if(config == null) config = service.getPrinter(this.getClientIp(httpRequest));
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_PRINTER_CONFIG_SUCCESS);
        return new Response<PrinterConfigDTO>().withData(config);
    }


    private final String LOCALHOST_IPV4 = "127.0.0.1";
    private final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        String pcName = request.getRemoteHost();
        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                    pcName = inetAddress.getHostName();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        if(!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return pcName + "." + ipAddress;
    }


}
