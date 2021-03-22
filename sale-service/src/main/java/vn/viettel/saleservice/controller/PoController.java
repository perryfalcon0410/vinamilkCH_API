package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.PoService;
import vn.viettel.saleservice.service.dto.*;
import vn.viettel.saleservice.service.impl.POExportExcel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sale/po")
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
    @GetMapping("/po-promotion-detail/{poId}")
    public Response<List<PoPromotionalDetailDTO>> getListPoPromotionDetailByPoId(@PathVariable Long poId) {
        return poService.getListPromotionDetailByPoId(poId);
    }
    @PutMapping("/all-po-confirm/{poId}")
    public void changeStatusPo(@PathVariable Long poId) {
         poService.changeStatusPo(poId);
    }
    @GetMapping("/export/excel/{poId}")
    public void exportToExcel(HttpServletResponse response, @PathVariable Long poId) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<SoConfirmDTO> soConfirmList = poService.getProductSoConfirm(poId).getData();
        List<SoConfirmDTO> soConfirmList2 = poService.getProductPromotinalSoConfirm(poId).getData();

         POExportExcel poExportExcel = new POExportExcel(soConfirmList,soConfirmList2);

        poExportExcel.export(response);
    }
}
