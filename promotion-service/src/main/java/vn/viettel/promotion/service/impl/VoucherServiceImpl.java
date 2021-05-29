package vn.viettel.promotion.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.ShopParamRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.AvailableTimeUtils;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.entities.VoucherProgram;
import vn.viettel.promotion.entities.VoucherSaleProduct;
import vn.viettel.promotion.entities.VoucherShopMap;
import vn.viettel.promotion.messaging.VoucherFilter;
import vn.viettel.promotion.repository.*;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.feign.CustomerClient;
import vn.viettel.promotion.service.feign.ShopClient;
import vn.viettel.promotion.service.feign.UserClient;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
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

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerClient customerClient;

    @Override
    public Response<Page<VoucherDTO>> findVouchers(VoucherFilter filter, Pageable pageable) {
        ShopParamDTO shopParamDTO = shopClient.getShopParamV1("SALEMT_LIMITVC", "LIMITVC", filter.getShopId()).getData();
        LocalDate updateAtDB = new Timestamp(shopParamDTO.getUpdatedAt().getTime())
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate dateNow =  LocalDate.now();
        Integer maxNumber = Integer.valueOf(shopParamDTO.getName());
        Integer currentNumber = Integer.valueOf(shopParamDTO.getDescription()!=null?shopParamDTO.getDescription():"0");
        if( maxNumber.equals(currentNumber)) throw new ValidateException(ResponseMessage.CANNOT_SEARCH_VOUCHER);

        Page<Voucher> vouchers = repository.findVouchers(filter.getKeyWord(),
                DateUtils.convertToDate(new Date()), DateUtils.convertFromDate(new Date()), pageable);
        if(vouchers.getContent().isEmpty()) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ShopParamRequest request = modelMapper.map(shopParamDTO, ShopParamRequest.class);
            if(updateAtDB.isEqual(dateNow) )
                request.setDescription(Integer.toString(Integer.valueOf(request.getDescription()) + 1));
            else request.setDescription("1");
            shopClient.updateShopParamV1(request, shopParamDTO.getId());
        }

        Page<VoucherDTO> voucherDTOs = vouchers.map(voucher -> this.mapVoucherToVoucherDTO(voucher));
        return new Response<Page<VoucherDTO>>().withData(voucherDTOs);
    }

    @Override
    public Response<VoucherDTO> getVoucher(Long id, Long shopId, Long customerId, List<Long> productIds) {
        Voucher voucher = repository.getByIdAndStatusAndIsUsed(id, 1, false)
            .orElseThrow(() -> new ValidateException(ResponseMessage.VOUCHER_DOES_NOT_EXISTS));
        CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
        if(customer == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        ShopDTO shopDTO = shopClient.getByIdV1(shopId).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        if(voucher.getShopId() != null && !voucher.getShopId().equals(shopId))
            throw new ValidateException(ResponseMessage.VOUCHER_SHOP_MAP_REJECT);

        VoucherShopMap voucherMap = voucherShopMapRepo.checkVoucherShopMap(voucher.getVoucherProgramId(), shopDTO.getId()).orElse(null);
        if(voucherMap == null && shopDTO.getParentShopId() != null) {
            voucherShopMapRepo.checkVoucherShopMap(voucher.getVoucherProgramId(), shopDTO.getParentShopId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.VOUCHER_SHOP_MAP_REJECT));
        }

        if(voucher.getCustomerTypeId() != null && !voucher.getCustomerTypeId().equals(customer.getCustomerTypeId()))
            throw new ValidateException(ResponseMessage.VOUCHER_CUSTOMER_TYPE_REJECT);
        voucherCustomerMapRepo.checkVoucherCustomerMap(voucher.getVoucherProgramId(), customer.getCustomerTypeId())
            .orElseThrow(() -> new ValidateException(ResponseMessage.VOUCHER_CUSTOMER_TYPE_REJECT));

        if(voucher.getCustomerId() != null && !voucher.getCustomerId().equals(customerId))
            throw new ValidateException(ResponseMessage.VOUCHER_CUSTOMER_REJECT);

        List<VoucherSaleProduct> products =
                voucherSaleProductRepo.findByVoucherProgramIdAndStatus(voucher.getVoucherProgramId(), 1);
        List<Long> mapProductIds = products.stream().map(VoucherSaleProduct::getProductId).collect(Collectors.toList());
        if(!productIds.containsAll(mapProductIds))
            throw new ValidateException(ResponseMessage.VOUCHER_PRODUCT_REJECT);

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
            voucherSaleProductRepo.findByVoucherProgramIdAndStatus(programId, 1);
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
            voucherDTO.setActiveTime(parseToStringDate(voucherProgram.getFromDate()));
            if(voucherProgram.getToDate() != null)
                voucherDTO.setActiveTime(voucherDTO.getActiveTime() + "-" + parseToStringDate(voucherProgram.getToDate()));
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
