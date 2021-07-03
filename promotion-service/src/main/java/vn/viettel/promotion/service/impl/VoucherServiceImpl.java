package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.ShopParamRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.*;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;
import vn.viettel.promotion.service.feign.UserClient;

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
    UserClient userClient;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerClient customerClient;

    @Override
    public VoucherDTO getVoucherBySerial(String serial, Long shopId, Long customerId, List<Long> productIds) {
        if(serial != null) serial = serial.trim();
        ShopParamDTO shopParamDTO = shopClient.getShopParamV1("SALEMT_LIMITVC", "LIMITVC", shopId).getData();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Integer maxNumber = Integer.valueOf(shopParamDTO.getName()!=null?shopParamDTO.getName():"0");
        Integer currentNumber = Integer.valueOf(shopParamDTO.getDescription()!=null?shopParamDTO.getDescription():"0");
        if(currentNumber > maxNumber) {
            VoucherDTO voucherDTO = new VoucherDTO();
            voucherDTO.setIsLocked(true);
            voucherDTO.setMessage(ResponseMessage.CANNOT_SEARCH_VOUCHER.statusCodeValue());
            return voucherDTO;
        }

        Voucher voucher = repository.getBySerial(serial,
                DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now())).orElse(null);

        if(voucher == null) {
            ShopParamRequest request = modelMapper.map(shopParamDTO, ShopParamRequest.class);
            if(shopParamDTO.getUpdatedAt() != null && shopParamDTO.getUpdatedAt().toLocalDate().isEqual(LocalDate.now()) )
                request.setDescription(Integer.toString( currentNumber+ 1));
            else request.setDescription("1");
            shopClient.updateShopParamV1(request, shopParamDTO.getId());

            if(Integer.valueOf(request.getDescription()) > maxNumber) throw new ValidateException(ResponseMessage.CANNOT_SEARCH_VOUCHER);

            throw new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS);
        }

        this.validVoucher(customerId, shopId, voucher, productIds);

        VoucherDTO voucherDTO = this.mapVoucherToVoucherDTO(voucher);
        voucherDTO.setIsLocked(false);
        return voucherDTO;

    }

    private void validVoucher(Long customerId, Long shopId, Voucher voucher, List<Long> productIds) {

        CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
        if(customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        if(voucher.getShopId() != null && !voucher.getShopId().equals(shopId))
            throw new ValidateException(ResponseMessage.VOUCHER_SHOP_MAP_REJECT);

        List<Long> shopMapIds = voucherShopMapRepo.findShopIds(voucher.getVoucherProgramId(), 1);
        if(!shopMapIds.isEmpty() && !shopMapIds.contains(shopDTO.getId())
            && (shopDTO.getParentShopId() == null || (shopDTO.getParentShopId() != null && !shopMapIds.contains(shopDTO.getParentShopId()))))
            throw new ValidateException(ResponseMessage.VOUCHER_SHOP_MAP_REJECT);

        if(voucher.getCustomerTypeId() != null && !voucher.getCustomerTypeId().equals(customer.getCustomerTypeId()))
            throw new ValidateException(ResponseMessage.VOUCHER_CUSTOMER_TYPE_REJECT);
        List<Long> customerMapIds = voucherCustomerMapRepo.findCustomerIds(voucher.getVoucherProgramId(), 1);
        if(!customerMapIds.isEmpty() && !customerMapIds.contains(customer.getCustomerTypeId()))
            throw new ValidateException(ResponseMessage.VOUCHER_CUSTOMER_TYPE_REJECT);

        if(voucher.getCustomerId() != null && !voucher.getCustomerId().equals(customerId))
            throw new ValidateException(ResponseMessage.VOUCHER_CUSTOMER_REJECT);

        List<Long> mapProductIds =  voucherSaleProductRepo.findProductIds(voucher.getVoucherProgramId(), 1);
        if(!mapProductIds.isEmpty() && !productIds.containsAll(mapProductIds))
            throw new ValidateException(ResponseMessage.VOUCHER_PRODUCT_REJECT);
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
    public List<VoucherSaleProductDTO> findVoucherSaleProducts(Long programId) {
        List<VoucherSaleProduct> products =
            voucherSaleProductRepo.findByVoucherProgramIdAndStatus(programId, 1);
        List<VoucherSaleProductDTO> dto = products.stream().map(product -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(product, VoucherSaleProductDTO.class);
        }).collect(Collectors.toList());

        return dto;
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
