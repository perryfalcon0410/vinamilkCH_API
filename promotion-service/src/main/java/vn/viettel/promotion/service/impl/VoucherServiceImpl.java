package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.ShopParamRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.entities.VoucherProgram;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    ShopClient shopClient;

    @Autowired
    CustomerClient customerClient;

    @Override
    public VoucherDTO getVoucherBySerial(String serial, Long shopId, Long customerId, List<Long> productIds) {
        if(serial != null && !serial.isEmpty()) serial = serial.trim().toUpperCase();
        ShopParamDTO shopParamDTO = shopClient.getShopParamV1("SALEMT_LIMITVC", "LIMITVC", shopId).getData();
        if(shopParamDTO == null) throw new ValidateException(ResponseMessage.APPARAM_VOUCHER_NOT_EXITS);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Integer maxNumber = Integer.valueOf(shopParamDTO.getName()!=null?shopParamDTO.getName():"0");
        Integer currentNumber = Integer.valueOf(shopParamDTO.getDescription()!=null?shopParamDTO.getDescription():"0");
        if(currentNumber > maxNumber) throw new ValidateException(ResponseMessage.CANNOT_SEARCH_VOUCHER);

        Voucher voucher = repository.getBySerial(serial,
                DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now())).orElse(null);

        ResponseMessage message = this.validVoucher(customerId, shopId, voucher, productIds);
        if(message != null) {
            ShopParamRequest request = modelMapper.map(shopParamDTO, ShopParamRequest.class);
            if(shopParamDTO.getUpdatedAt() != null && shopParamDTO.getUpdatedAt().toLocalDate().isEqual(LocalDate.now()) )
                request.setDescription(Integer.toString( currentNumber+ 1));
            else request.setDescription("1");
            shopClient.updateShopParamV1(request, shopParamDTO.getId());

            if(Integer.valueOf(request.getDescription()) > maxNumber) throw new ValidateException(ResponseMessage.CANNOT_SEARCH_VOUCHER);

            throw new ValidateException(message);
        }

        VoucherDTO voucherDTO = this.mapVoucherToVoucherDTO(voucher);
        return voucherDTO;

    }

    private ResponseMessage validVoucher(Long customerId, Long shopId, Voucher voucher, List<Long> productIds) {
        if(voucher == null) return ResponseMessage.VOUCHER_DOES_NOT_EXISTS;

        CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
        if(customer == null) return  ResponseMessage.CUSTOMER_DOES_NOT_EXIST;

        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        if(voucher.getShopId() != null && !voucher.getShopId().equals(shopId))
        return ResponseMessage.VOUCHER_SHOP_MAP_REJECT;

        List<Long> shopMapIds = voucherShopMapRepo.findShopIds(voucher.getVoucherProgramId(), 1);
        if(!shopMapIds.isEmpty() && !shopMapIds.contains(shopDTO.getId())
            && (shopDTO.getParentShopId() == null || (shopDTO.getParentShopId() != null && !shopMapIds.contains(shopDTO.getParentShopId()))))
            return ResponseMessage.VOUCHER_SHOP_MAP_REJECT;

        if(voucher.getCustomerTypeId() != null && !voucher.getCustomerTypeId().equals(customer.getCustomerTypeId()))
            return ResponseMessage.VOUCHER_CUSTOMER_TYPE_REJECT;
        List<Long> customerMapIds = voucherCustomerMapRepo.findCustomerIds(voucher.getVoucherProgramId(), 1);
        if(!customerMapIds.isEmpty() && !customerMapIds.contains(customer.getCustomerTypeId()))
            return ResponseMessage.VOUCHER_CUSTOMER_TYPE_REJECT;

        if(voucher.getCustomerId() != null && !voucher.getCustomerId().equals(customerId))
            return ResponseMessage.VOUCHER_CUSTOMER_REJECT;

        List<Long> mapProductIds =  voucherSaleProductRepo.findProductIds(voucher.getVoucherProgramId(), 1);
        if(!mapProductIds.isEmpty() && !productIds.containsAll(mapProductIds))
            return ResponseMessage.VOUCHER_PRODUCT_REJECT;
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoucherDTO updateVoucher(VoucherDTO voucherDTO) {
        Voucher voucherOld = repository.getById(voucherDTO.getId());
        if(voucherOld == null)
            throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Voucher voucher = modelMapper.map(voucherDTO, Voucher.class);
        repository.save(voucher);

        return this.mapVoucherToVoucherDTO(voucher);
    }


    @Override
    public VoucherDTO getFeignVoucher(Long id) {
        Voucher voucher = repository.getById(id);
        if(voucher == null) throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        VoucherDTO voucherDTO = modelMapper.map(voucher, VoucherDTO.class);
        return voucherDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean returnVoucher(Long saleOrderId) {
        List<Voucher> vouchers = repository.getVoucherBySaleOrderId(saleOrderId);
        for(Voucher voucher: vouchers) {
            voucher.setIsUsed(false);
            voucher.setSaleOrderId(null);
            voucher.setOrderNumber(null);
            voucher.setPriceUsed(null);
            voucher.setOrderCustomerCode(null);
            voucher.setOrderShopCode(null);
            voucher.setOrderAmount(null);
            voucher.setOrderDate(null);
        }

        return true;
    }

    private VoucherDTO mapVoucherToVoucherDTO(Voucher voucher) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        VoucherDTO voucherDTO = modelMapper.map(voucher, VoucherDTO.class);
        VoucherProgram voucherProgram = voucherProgramRepo.findById(voucherDTO.getVoucherProgramId()).orElse(null);
        if(voucherProgram != null) {
            voucherDTO.setVoucherProgramCode(voucherProgram.getVoucherProgramCode());
            voucherDTO.setVoucherProgramName(voucherProgram.getVoucherProgramName());
            voucherDTO.setActiveTime(DateUtils.formatDate2StringDate(voucherProgram.getFromDate()));
            if(voucherProgram.getToDate() != null)
                voucherDTO.setActiveTime(voucherDTO.getActiveTime() + "-" + DateUtils.formatDate2StringDate(voucherProgram.getToDate()));
        }
        return voucherDTO;
    }

    public List<VoucherDTO> getVoucherBySaleOrderId(long id) {
        List<Voucher> vouchers = voucherRepository.getVoucherBySaleOrderId(id);
       List<VoucherDTO> response =
            vouchers.stream().map(this::mapVoucherToVoucherDTO).collect(Collectors.toList());
        return response;
    }

}
