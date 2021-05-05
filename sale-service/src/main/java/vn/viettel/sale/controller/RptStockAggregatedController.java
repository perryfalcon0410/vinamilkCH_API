package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.entities.RptStockAggregated;
import vn.viettel.sale.service.RptStockAggregatedService;

import java.util.Date;

@RestController
public class RptStockAggregatedController extends BaseController {

    private final String root = "/sales/rpt-stock-aggregated";

    @Autowired
    RptStockAggregatedService rptStockAggregatedService;

    //test select rpt-stock-aggregated work
//    @RoleAdmin
//    @GetMapping(value = { V1 + root })
//    public RptStockAggregated findProductsCustomerTopSale(@RequestParam Long warehouseTypeId, @RequestParam Long productId, @RequestParam Date rptDate) {
//        return rptStockAggregatedService.getRptStockAggregated(this.getShopId(), warehouseTypeId, productId, rptDate);
//    }

}
