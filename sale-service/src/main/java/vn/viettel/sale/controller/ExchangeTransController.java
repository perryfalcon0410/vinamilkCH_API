package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng cho quản lý đổi hàng hỏng")
public class ExchangeTransController extends BaseController {
    @Autowired
    ExchangeTranService service;
    private final String root = "/sales/exchangetrans";

    @ApiOperation(value = "Api dùng khi tạo mới đơn đổi hàng để lấy lý do trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 9021, message = "Lý do đổi trả không hợp lệ")
    })
    @GetMapping(value = { V1 + root + "/reasons"})
    public Response<List<CategoryDataDTO>> getAllReason() {
        return service.getReasons();
    }

    @ApiOperation(value = "Api dùng để lấy danh sách đơn đổi hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root})
    public Response<Page<ExchangeTransDTO>> getAllExchangeTrans(@RequestParam(required = false) String transCode,
                                                                @RequestParam(required = false) Date fromDate,
                                                                @RequestParam(required = false) Date toDate, @RequestParam(required = false) Long reasonId, Pageable pageable) {
        return service.getAllExchange(this.getRoleId(), this.getShopId(), transCode, fromDate, toDate, reasonId, pageable);
    }

    @ApiOperation(value = "Api dùng để tạo mới đơn đổi hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })
    @PostMapping(value = { V1 + root + "/create"})
    public Response<ExchangeTrans> create(@Valid @RequestBody ExchangeTransRequest request) {
        return service.create(request, this.getUserId(),this.getShopId());
    }
    @ApiOperation(value = "Api lấy 1 đơn đổi trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ExchangeTransDTO> getExchangeTrans(@PathVariable Long id) {
        return service.getExchangeTrans(id);
    }
    @ApiOperation(value = "Api chỉnh sửa đơn đổi trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })
    @PutMapping(value = { V1 + root + "/update/{id}"})
    public Response<String> update(@PathVariable Long id,@RequestBody  ExchangeTransRequest request) {
        return service.update(id,request,this.getShopId());
    }

    @ApiOperation(value = "Api dùng để lấy danh sách hàng hỏng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
    })
    @GetMapping(V1 + root + "/products/{id}")
    public Response<List<ExchangeTransDetailRequest>> getBrokenProducts(@PathVariable Long id) {
        return service.getBrokenProducts(id);
    }
}
