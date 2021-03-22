package vn.viettel.sale.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.CommonService;
import vn.viettel.sale.service.dto.ReasonDTO;
import vn.viettel.sale.service.dto.ShopDTO;
import vn.viettel.sale.service.impl.InvoiceReport;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class CommonController {
    @Autowired
    CommonService commonService;
    @Autowired
    InvoiceReport invoiceReport;
    @GetMapping("/reason")
    public Response<List<ReasonDTO>> getAllReason() {
        return commonService.getAllReason();
    }
    @GetMapping("/shop/{shopId}")
    public Response<ShopDTO> getShopById(@PathVariable Long shopId) {
        return commonService.getShopById(shopId);
    }
    @GetMapping("/report/{format}/{idRe}")
    public String exportReport(@PathVariable String format,@PathVariable Long idRe) throws FileNotFoundException, JRException {
        return invoiceReport.exportReport(format,idRe);
    }
}
