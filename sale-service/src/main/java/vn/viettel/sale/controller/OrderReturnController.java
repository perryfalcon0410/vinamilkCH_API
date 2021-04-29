package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.OrderReturnTotalResponse;
import vn.viettel.sale.messaging.SaleOrderChosenFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.util.Date;
import java.util.List;

@RestController
public class OrderReturnController extends BaseController {
    @Autowired
    OrderReturnService orderReturnService;
    private final String root = "/sales/order-return";
    @RoleAdmin
    @GetMapping(value = { V1 + root } )
    public Response<CoverResponse<Page<OrderReturnDTO>, OrderReturnTotalResponse>> getAllOrderReturn(@RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                                     @RequestParam(value = "returnNumber", required = false) String orderNumber,
                                                                                                     @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                                     @RequestParam(value = "toDate", required = false) Date toDate,Pageable pageable) {
        SaleOrderFilter filter = new SaleOrderFilter(searchKeywords, orderNumber, null, fromDate, toDate);
        return orderReturnService.getAllOrderReturn(filter, pageable);
    }
    @RoleAdmin
    @GetMapping(value = { V1 + root + "/detail/{id}"})
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(@PathVariable long id) {
        return orderReturnService.getOrderReturnDetail(id);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/choose"})
    public Response<List<SaleOrderDTO>> selectForReturn(@RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
                                                        @RequestParam(value = "searchKeywords", required = false, defaultValue = "") String searchKeywords,
                                                        @RequestParam(value = "product", required = false, defaultValue = "") String product,
                                                        @RequestParam(value = "fromDate", required = false, defaultValue = "") Date fromDate,
                                                        @RequestParam(value = "toDate", required = false, defaultValue = "") Date toDate) {
        SaleOrderChosenFilter filter = new SaleOrderChosenFilter(orderNumber, searchKeywords, product, fromDate, toDate);
        return orderReturnService.getSaleOrderForReturn(filter);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/chosen/{id}"})
    public Response<OrderReturnDetailDTO> orderSelected(@PathVariable long id) {
        return orderReturnService.getSaleOrderChosen(id);
    }

    @RoleAdmin
    @PostMapping(value = { V1 + root })
    public Response<SaleOrder> createOrderReturn(@RequestBody OrderReturnRequest request) {
        return orderReturnService.createOrderReturn(request);
    }
}
