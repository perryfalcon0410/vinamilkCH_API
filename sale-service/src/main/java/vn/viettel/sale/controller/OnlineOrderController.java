package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

import java.util.Date;

@RestController
@RequestMapping("/api/sale")
public class OnlineOrderController extends BaseController {

    @Autowired
    OnlineOrderService onlineOrderService;

    /**
     *
     * @param searchKeywords search orderNumber
     * @param shopId id shop
     * @param synStatus 0 or 1
     * @param fromDate default start date of month
     * @param toDate default last date of month
     * @param pageable size, page
     * @return Response<Page<OnlineOrder>>>
     */
//    @RoleAdmin
    @GetMapping("/online-orders")
    public Response<Page<OnlineOrderDTO>> getOnlineOrders(@RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeywords,
                                                          @RequestParam(value = "shopId", required = false) Long shopId,
                                                          @RequestParam(value = "synStatus", required = false) Integer synStatus,
                                                          @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                          @RequestParam(value = "toDate", required = false) Date toDate,
                                                          Pageable pageable) {
        return onlineOrderService.getOnlineOrders(searchKeywords, shopId, synStatus, fromDate, toDate, pageable);
    }


}
