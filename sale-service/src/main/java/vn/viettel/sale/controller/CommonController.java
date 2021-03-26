package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.sale.service.CommonService;
import vn.viettel.sale.service.dto.ImportTypeDTO;
import vn.viettel.sale.service.dto.PoConfirmStatusDTO;
import vn.viettel.sale.service.dto.StockAdjustmentStatusDTO;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class CommonController {
    @Autowired
    CommonService commonService;

    @GetMapping("/import-type")
    public List<ImportTypeDTO> getImportType() {
        return commonService.getList();
    }
    @GetMapping("/poconfirm-status")
    public List<PoConfirmStatusDTO> getPoConfirmStatus() {
        return commonService.getListPoConfirmStatusDTO();
    }
    @GetMapping("/poconfirm-status")
    public List<StockAdjustmentStatusDTO> getStockAdjustmentStatus() {
        return commonService.getListStockAdjustmentTypeDTO();
    }
}
