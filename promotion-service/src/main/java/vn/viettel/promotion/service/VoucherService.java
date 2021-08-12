package vn.viettel.promotion.service;

import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface VoucherService extends BaseService {

    /*
     * Tìm voucher theo serial trong bán hàng
     */
    VoucherDTO getVoucherBySerial(String serial, Long shopId, Long customerId, List<Long> productIds);

    /*
     * Cập nhật lại voucher trong bán hàng gọi từ service sale
     */
    VoucherDTO updateVoucher(VoucherDTO voucherDTO);

    /*
     * Lấy danh sách voucher theo sale orde id
     */
    List<VoucherDTO> getVoucherBySaleOrderId(long id);

    /*
     * Lấy voucher theo id trong bán hàng gọi từ service sale
     */
    VoucherDTO getFeignVoucher(Long id);


}
