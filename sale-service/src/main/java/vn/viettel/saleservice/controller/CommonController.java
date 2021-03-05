package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.CommonService;
import vn.viettel.saleservice.service.dto.ReasonDTO;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.ReceiptSearch;

import java.util.List;

@RestController
@RequestMapping("/api/reason")
public class CommonController {
    @Autowired
    CommonService commonService;
    @GetMapping("/all")
    public Response<List<ReasonDTO>> getAllReason() {
        return commonService.getAllReason();
    }
}
