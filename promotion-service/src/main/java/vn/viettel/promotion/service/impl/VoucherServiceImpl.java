package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.entities.VoucherProgram;
import vn.viettel.promotion.entities.VoucherSaleProduct;
import vn.viettel.promotion.messaging.VoucherFilter;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.feign.UserClient;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl extends BaseServiceImpl<Voucher, VoucherRepository> implements VoucherService {

    @Autowired
    VoucherProgramRepository voucherProgramRepo;

    @Autowired
    VoucherShopMapRepostiory voucherShopMapRepo;

    @Autowired
    VoucherCustomerMapRepository voucherCustomerMapRepo;

    @Autowired
    VoucherSaleProductRepository voucherSaleProductRepo;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    UserClient userClient;

    @Override
    public Response<Page<VoucherDTO>> findVouchers(VoucherFilter voucherFilter, Pageable pageable) {
        Page<Voucher> vouchers = repository.findVouchers(voucherFilter.getKeyWord(), pageable);
        Page<VoucherDTO> voucherDTOs = vouchers.map(voucher -> this.mapVoucherToVoucherDTO(voucher));
        return new Response<Page<VoucherDTO>>().withData(voucherDTOs);
    }

    @Override
    public Response<VoucherDTO> getVoucher(Long id, Long shopId, Long customerTypeId) {
        Voucher voucher = repository.getById(id);
        if(voucher == null)
            throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);

        voucherShopMapRepo.checkVoucherShopMap(voucher.getVoucherProgramId(), shopId)
            .orElseThrow(() -> new ValidateException(ResponseMessage.VOUCHER_SHOP_MAP_REJECT));

        voucherCustomerMapRepo.checkVoucherCustomerMap(voucher.getVoucherProgramId(), customerTypeId)
            .orElseThrow(() -> new ValidateException(ResponseMessage.VOUCHER_CUSTOMER_REJECT));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        VoucherDTO voucherDTO = this.mapVoucherToVoucherDTO(voucher);
        return new Response<VoucherDTO>().withData(voucherDTO);
    }

    @Override
    public Response<Voucher> getFeignVoucher(Long id) {
        Voucher voucher = repository.getById(id);
        if(voucher == null) throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);
        return new Response<Voucher>().withData(voucher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<VoucherDTO> updateVoucher(VoucherDTO voucherDTO) {
        Voucher voucherOld = repository.getById(voucherDTO.getId());
        if(voucherOld == null)
            throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Voucher voucher = modelMapper.map(voucherDTO, Voucher.class);
        repository.save(voucher);

        return new Response<VoucherDTO>().withData(this.mapVoucherToVoucherDTO(voucher));
    }

    @Override
    public Response<List<VoucherSaleProductDTO>> findVoucherSaleProducts(Long programId) {
        List<VoucherSaleProduct> products =
            voucherSaleProductRepo.findVoucherSaleProductByVoucherProgramIdAndStatus(programId, 1);
        List<VoucherSaleProductDTO> dto = products.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, VoucherSaleProductDTO.class);
        }).collect(Collectors.toList());

        return new Response<List<VoucherSaleProductDTO>>().withData(dto);
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

    public Response<List<VoucherDTO>> getVoucherBySaleOrderId(long id) {
        List<Voucher> vouchers = voucherRepository.getVoucherBySaleOrderId(id);
       List<VoucherDTO> response =
            vouchers.stream().map(this::mapVoucherToVoucherDTO).collect(Collectors.toList());
        return new Response<List<VoucherDTO>>().withData(response);
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
