package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.TotalDTO;
import vn.viettel.sale.service.impl.ComboProductTransServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API xuất nhập sản phẩm combo")
@Slf4j
public class ComboProductTransController extends BaseController {

    @Autowired
    ComboProductTransService comboProductTransService;

    @Autowired
    private JMSSender jmsSender;
    
    private final String root = "/sales/combo-product-trans";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Tìm kiếm xuất nhập sản phẩm combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ComboProductTranDTO>, TotalDTO>> findComboProductTrans(HttpServletRequest request,
                                      @ApiParam("Tìm mã giao dịch")
                                      @RequestParam(value = "transCode", required = false) String transCode,
                                      @ApiParam("Loại giao dịch")
                                      @RequestParam(value = "transType", required = false) Integer transType,
                                      @RequestParam(value = "fromDate") Date fromDate,
                                      @RequestParam(value = "toDate") Date toDate,
                                      @SortDefault.SortDefaults({
                                          @SortDefault(sort = "transDate", direction = Sort.Direction.DESC),
                                      })
                                      Pageable pageable) {

        ComboProductTranFilter filter = new ComboProductTranFilter(this.getShopId(request), transCode, transType, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
        CoverResponse<Page<ComboProductTranDTO>, TotalDTO> response = comboProductTransService.getAll(filter, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.FIND_COMBO_PRODUCTS_TRANS_SUCCESS);
        return new Response<CoverResponse<Page<ComboProductTranDTO>, TotalDTO>>().withData(response);
    }

    @PostMapping(value = { V1 + root} )
    @ApiOperation(value = "Tạo mới xuất nhập sản phẩm combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> create(HttpServletRequest request, @Valid @ApiParam("Thông tin tạo mới xuất, nhập combo") @RequestBody ComboProductTranRequest comboRequest) {
        ComboProductTranDTO dto = comboProductTransService.create(comboRequest, this.getShopId(request), this.getUsername(request));
    	if(dto != null && dto.getId() != null) {
    		sendSynRequest(JMSType.combo_product_trans, Arrays.asList(dto.getId()));
    	}
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.CREATE_COMBO_PRODUCT_TRANS_SUCCESS);
        Response response = new Response();
        response.setStatusValue(ResponseMessage.CREATED_SUCCESSFUL.statusCodeValue());
        return response;
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn xuất nhập sản phẩm combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<ComboProductTranDTO> getComboProductTran(HttpServletRequest request, @PathVariable Long id) {
        ComboProductTranDTO response = comboProductTransService.getComboProductTrans(id);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_COMBO_PRODUCT_TRANS_SUCCESS);
        return new Response<ComboProductTranDTO>().withData(response);
    }
    
    private void sendSynRequest(String type, List<Long> listId) {
        try {
            jmsSender.sendMessage(type, listId);
        } catch (Exception ex) {
            log.error("khoi tao jmsSender", ex);
        }
    }
}
