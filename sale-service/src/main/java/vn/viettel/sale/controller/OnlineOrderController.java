package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

@RestController
@RequestMapping("/api/sale/online-orders")
public class OnlineOrderController extends BaseController {

    @Autowired
    OnlineOrderService onlineOrderService;

    @RoleAdmin
    @GetMapping
    public Response<Page<OnlineOrderDTO>> getOnlineOrders(@RequestBody OnlineOrderFilter filter,
                                                          Pageable pageable) {
        return onlineOrderService.getOnlineOrders(filter, pageable);
    }

//    @RoleAdmin
//    @GetMapping("/{id}")
//    public Response<OnlineOrderDTO> getOnlineOrder(@PathVariable Long id) {
//        return onlineOrderService.getOnlineOrder(id, this.getShopId());
//    }

}
