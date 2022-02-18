package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.sale.service.CommonService;
import vn.viettel.sale.service.dto.ImportTypeDTO;
import vn.viettel.sale.service.dto.PoConfirmStatusDTO;
import vn.viettel.sale.service.dto.StockAdjustmentStatusDTO;
import vn.viettel.sale.service.dto.StockBorrowingStatusDTO;

import java.util.List;

@RestController
public class CommonController extends BaseController {
    @Autowired
    CommonService commonService;
    private final String root = "/sales/commons";

    public void setService(CommonService service){
        if(commonService == null) commonService = service;
    }

    @GetMapping(value = { V1 + root + "/import-type"})
    public List<ImportTypeDTO> getImportType() {
        return commonService.getList();
    }

    @GetMapping(value = { V1 + root + "/po-confirm-status"})
    public List<PoConfirmStatusDTO> getPoConfirmStatus() {
        return commonService.getListPoConfirmStatusDTO();
    }

    @GetMapping(value = { V1 + root + "/adjustment-status"})
    public List<StockAdjustmentStatusDTO> getStockAdjustmentStatus() {
        return commonService.getListStockAdjustmentTypeDTO();
    }

    @GetMapping(value = { V1 + root + "/borrowing-status"})
    public List<StockBorrowingStatusDTO> getStockBorrowingStatus() {
        return commonService.getListStockBorrowingTypeDTO();
    }
}
