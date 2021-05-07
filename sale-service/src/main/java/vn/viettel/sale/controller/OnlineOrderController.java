package vn.viettel.sale.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

import java.util.Date;

@RestController
public class OnlineOrderController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    OnlineOrderService onlineOrderService;
    private final String root = "/sales/online-orders";

    @RoleAdmin
    @GetMapping(value = { V1 + root } )
    public Response<Page<OnlineOrderDTO>> getOnlineOrders(@RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
                                                          @RequestParam(value = "synStatus", required = false) Integer synStatus,
                                                          @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                          @RequestParam(value = "toDate", required = false) Date toDate,
                                                          Pageable pageable) {
        logger.info("[getOnlineOrders()] - onlineOder getOnlineOrders #user_id: {}, #orderNumber: {}, #synStatus: {}, " +
                "#fromDate: {}, #toDate: {}", this.getUserId(), orderNumber, synStatus, fromDate, toDate );
        OnlineOrderFilter filter = new OnlineOrderFilter(orderNumber, this.getShopId(), synStatus, fromDate, toDate);
        return onlineOrderService.getOnlineOrders(filter, pageable);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<OnlineOrderDTO> getOnlineOrder(@PathVariable Long id) {
        logger.info("[getOnlineOrder()] - onlineOder getOnlineOrder #user_id: {}, #id: {}", this.getUserId(), id);
        return onlineOrderService.getOnlineOrder(id, this.getShopId(), this.getUserId());
    }
}
