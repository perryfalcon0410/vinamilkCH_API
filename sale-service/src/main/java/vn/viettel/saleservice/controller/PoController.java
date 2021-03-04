package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.PoService;
import vn.viettel.saleservice.service.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/po")
public class PoController {
    @Autowired
    PoService poService;

    @GetMapping("/all-po-confirm")
    public Response<List<POConfirmDTO>> getAllPoConfirm() {
        return poService.getAllPoConfirm();
    }
    @GetMapping("/all-po-adjusted")
    public Response<List<POAdjustedDTO>> getAllPoAdjusted() {
        return poService.getAllPoAdjusted();
    }
    @GetMapping("/all-po-borrow")
    public Response<List<POBorrowDTO>> getAllPoBorrow() {
        return poService.getAllPoBorrow();
    }
    @GetMapping("/po-adjusted-detail0/{paId}")
    public Response<List<PoAdjustedDetailDTO>> getPoAdjustedDetail(@PathVariable Long paId) {
        return poService.getPoAdjustedDetail(paId);
    }
    @GetMapping("/po-adjusted-detail1/{paId}")
    public Response<List<PoAdjustedDetailDTO>> getPoAdjustedDetailDiscount(@PathVariable Long paId) {
        return poService.getPoAdjustedDetailDiscount(paId);
    }
    @GetMapping("/so-confirm0/{paId}")
    public Response<List<SoConfirmDTO>> getProductSoConfirm(@PathVariable Long paId) {
        return poService.getProductSoConfirm(paId);
    }
    @GetMapping("/so-confirm1/{paId}")
    public Response<List<SoConfirmDTO>> getProductPromotinalSoConfirm(@PathVariable Long paId) {
        return poService.getProductPromotinalSoConfirm(paId);
    }
    @GetMapping("/po-borrow-detail0/{paId}")
    public Response<List<PoBorrowDetailDTO>> getProductPoBorrowDetail(@PathVariable Long paId) {
        return poService.getProductPoBorrowDetail(paId);
    }
    @GetMapping("/po-borrow-detail1/{paId}")
    public Response<List<PoBorrowDetailDTO>> getProductPromotinalPoBorrowDetail(@PathVariable Long paId) {
        return poService.getProductPromotinalPoBorrowDetail(paId);
    }

}
