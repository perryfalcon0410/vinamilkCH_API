package vn.viettel.common.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    public Response<PrinterConfigDTO> update(HttpServletRequest httpRequest, @PathVariable String clientId ) {
        PrinterConfigDTO config = service.getPrinter(clientId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_PRINTER_CONFIG_SUCCESS);
        return new Response<PrinterConfigDTO>().withData(config);
    }



}
