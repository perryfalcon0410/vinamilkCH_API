package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.db.entity.voucher.VoucherProgram;
import vn.viettel.core.db.entity.voucher.VoucherSaleProduct;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.messaging.VoucherUpdateRequest;
import vn.viettel.promotion.repository.VoucherProgramRepository;
import vn.viettel.promotion.repository.VoucherRepository;
import vn.viettel.promotion.repository.VoucherSaleProductRepository;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.dto.VoucherDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class VoucherServiceImpl extends BaseServiceImpl<Voucher, VoucherRepository> implements VoucherService {

    @Autowired
    VoucherProgramRepository voucherProgramRepo;

    @Autowired
    VoucherSaleProductRepository voucherSaleProductRepo;

    @Override
    public Response<Page<VoucherDTO>> findVouchers(String keyWord, Long shopId, Long customerTypeId, Pageable pageable) {
        Page<Voucher> vouchers = repository.findVouchers(keyWord, shopId, customerTypeId, pageable);
        Page<VoucherDTO> voucherDTOs = vouchers.map(voucher -> this.mapVoucherToVoucherDTO(voucher));
        return new Response<Page<VoucherDTO>>().withData(voucherDTOs);
    }

    @Override
    public Response<Voucher> getVoucher(Long id) {
        Voucher voucher = repository.findByIdAndDeletedAtIsNull(id);
        if(voucher == null) throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);
        return new Response<Voucher>().withData(voucher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<VoucherDTO> updateVoucher(Long id, VoucherUpdateRequest request, String username) {
        Voucher voucherOld = repository.findByIdAndDeletedAtIsNull(id);
        if(voucherOld == null)
            throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Voucher voucher = modelMapper.map(request, Voucher.class);
        voucher.setId(id);
        voucher.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        voucher.setUpdateUser(username);
        voucher = repository.save(voucher);

        return new Response<VoucherDTO>().withData(this.mapVoucherToVoucherDTO(voucher));
    }

    @Override
    public Response<List<VoucherSaleProduct>> findVoucherSaleProducts(Long programId) {
        List<VoucherSaleProduct> products =
            voucherSaleProductRepo.findVoucherSaleProductByVoucherProgramIdAndStatus(programId, 1);

        return new Response<List<VoucherSaleProduct>>().withData(products);
    }

    private VoucherDTO mapVoucherToVoucherDTO(Voucher voucher) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        VoucherDTO voucherDTO = modelMapper.map(voucher, VoucherDTO.class);
        VoucherProgram voucherProgram = voucherProgramRepo.findById(voucherDTO.getVoucherProgramId()).orElse(null);
        if(voucherProgram != null) {
            voucherDTO.setVoucherProgramCode(voucherProgram.getVoucherProgramCode());
            voucherDTO.setVoucherProgramName(voucherProgram.getVoucherProgramName());
            voucherDTO.setActiveTime(parseToStringDate(voucherProgram.getFromDate()) + "-" + parseToStringDate(voucherProgram.getToDate()));
        }

        return voucherDTO;
    }

    public String parseToStringDate(Date date) {
        Calendar c = Calendar.getInstance();
        if (date == null) return null;
        c.setTime(date);
        String day = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH) + "";
        String month = c.get(Calendar.MONTH) + 1 < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1) + "";
        String year = c.get(Calendar.YEAR) + "";
        return day + "/" + month + "/" + year;
    }


}
