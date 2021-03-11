package vn.viettel.saleservice.service;

import vn.viettel.core.db.entity.POConfirm;
import vn.viettel.core.db.entity.PoPromotionalDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.*;

import java.util.List;

public interface PoService {
    Response<List<POConfirmDTO>> getAllPoConfirm();
    Response<List<POAdjustedDTO>> getAllPoAdjusted();
    Response<List<POBorrowDTO>> getAllPoBorrow();
    Response<List<PoAdjustedDetailDTO>> getPoAdjustedDetailDiscount(Long paId);
    Response<List<PoAdjustedDetailDTO>> getPoAdjustedDetail(Long paId);
    Response<List<SoConfirmDTO>> getProductPromotinalSoConfirm(Long paId);
    Response<List<SoConfirmDTO>> getProductSoConfirm(Long paId);
    Response<List<SoConfirmDTO>> getProductSoConfirm0();
    Response<List<SoConfirmDTO>> getProductPromotionalSoConfirm1();
    Response<List<PoBorrowDetailDTO>> getProductPromotinalPoBorrowDetail(Long paId);
    Response<List<PoBorrowDetailDTO>> getProductPoBorrowDetail(Long paId);
    void changeStatusPo(Long poId);
    Response<List<PoPromotionalDetailDTO>> getListPromotionDetailByPoId(Long paId);
}
