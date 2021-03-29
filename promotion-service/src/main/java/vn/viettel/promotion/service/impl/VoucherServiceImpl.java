package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl extends BaseServiceImpl<Voucher, VoucherRepository> implements VoucherService {

    @Autowired
    VoucherProgramRepository voucherProgramRepo;

    @Override
    public Response<List<VoucherDTO>> findVouchers(String keyWord, Long shopId, Long customerTypeId) {
        List<Voucher> vouchers = repository.findVouchers(keyWord, shopId, customerTypeId, new Date());
        List<VoucherDTO> voucherDTOs = vouchers.stream().map(voucher -> this.mapVoucherToVoucherDTO(voucher)).collect(Collectors.toList());
       for(VoucherDTO voucherDTO: voucherDTOs) {
           VoucherProgram voucherProgram = voucherProgramRepo.findById(voucherDTO.getVoucherProgramId()).orElse(null);
           if(voucherProgram != null) {
               voucherDTO.setVoucherProgramCode(voucherProgram.getVoucherProgramCode());
               voucherDTO.setVoucherProgramName(voucherProgram.getVoucherProgramName());
               voucherDTO.setActiveTime(parseToStringDate(voucherProgram.getFromDate()) + "-" + parseToStringDate(voucherProgram.getToDate()));
           }
       }
        return new Response<List<VoucherDTO>>().withData(voucherDTOs);
    }

    public String parseToStringDate(Date date) {
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

    private VoucherDTO mapVoucherToVoucherDTO(Voucher voucher) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        VoucherDTO voucherDTO = modelMapper.map(voucher, VoucherDTO.class);
        return voucherDTO;
    }

}
