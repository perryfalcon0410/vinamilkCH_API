package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.db.entity.voucher.VoucherProgram;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.repository.VoucherProgramRepository;
import vn.viettel.promotion.repository.VoucherRepository;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.dto.VoucherDTO;

import java.util.Calendar;
import java.util.Date;

@Service
public class VoucherServiceImpl extends BaseServiceImpl<Voucher, VoucherRepository> implements VoucherService {

    @Autowired
    VoucherProgramRepository voucherProgramRepo;

    @Override
    public Response<Page<VoucherDTO>> findVouchers(String keyWord, Long shopId, Long customerTypeId, Pageable pageable) {
        Page<Voucher> vouchers = repository.findVouchers(keyWord, shopId, customerTypeId, new Date(), pageable);
        Page<VoucherDTO> voucherDTOs = vouchers.map(voucher -> this.mapVoucherToVoucherDTO(voucher));

        return new Response<Page<VoucherDTO>>().withData(voucherDTOs);
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

    private String parseToStringDate(Date date) {
        Calendar c = Calendar.getInstance();
        if (date == null) {
            return null;
        }
        c.setTime(date);
        String day = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH) + "";
        String month = c.get(Calendar.MONTH) + 1 < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1) + "";
        String year = c.get(Calendar.YEAR) + "";
        return day + "/" + month + "/" + year;
    }

}
