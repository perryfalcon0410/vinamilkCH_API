package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.CommonService;
import vn.viettel.saleservice.service.dto.ReasonDTO;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.ReceiptSearch;
import vn.viettel.saleservice.service.dto.ShopDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommonController {
    @Autowired
    CommonService commonService;
    @GetMapping("/reason")
    public Response<List<ReasonDTO>> getAllReason() {
        return commonService.getAllReason();
    }
    @GetMapping("/shop/{shopId}")
    public Response<ShopDTO> getShopById(@PathVariable Long shopId) {
        return commonService.getShopById(shopId);
    }
}
