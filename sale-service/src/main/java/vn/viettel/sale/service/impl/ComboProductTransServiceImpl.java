package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ComboProductTranDetailRequest;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.ComboProductTransComboDTO;
import vn.viettel.sale.service.dto.ComboProductTransProductDTO;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ComboProductTranSpecification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComboProductTransServiceImpl
    extends BaseServiceImpl<ComboProductTrans, ComboProductTransRepository> implements ComboProductTransService {

    @Autowired
    ComboProductTransDetailRepository comboProductTransDetailRepo;

    @Autowired
    ComboProductRepository comboProductRepo;

    @Autowired
    ComboProductDetailRepository comboProductDetailRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    UserClient userClient;

    @Override
    public Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> findAll(ComboProductTranFilter filter, Pageable pageable) {

        Page<ComboProductTrans> comboProductTrans = repository.findAll(Specification.where(
                ComboProductTranSpecification.hasTransCode(filter.getTransCode())
                .and(ComboProductTranSpecification.hasTransType(filter.getTransType()))
                .and(ComboProductTranSpecification.hasShopId(filter.getShopId()))
                .and(ComboProductTranSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate()))
            ), pageable);

        Page<ComboProductTranDTO> pageProductTranDTOS = comboProductTrans.map(this::mapToOnlineOrderDTO);

        List<ComboProductTrans> transList = repository.findAll();
        TotalResponse totalResponse = new TotalResponse();
        transList.forEach(trans -> {
            totalResponse.addTotalPrice(trans.getTotalAmount()).addTotalQuantity(trans.getTotalQuantity());
        });

        CoverResponse coverResponse = new CoverResponse(pageProductTranDTOS, totalResponse);
        return new Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>>().withData(coverResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<ComboProductTranDTO> create(ComboProductTranRequest request, Long shopId, Long userId) {

        ComboProductTrans comboProductTran = this.createComboProductTransEntity(request, shopId, userId);
        repository.save(comboProductTran);

        List<ComboProductTransDetail> transDetails = this.createComboProductTransDetailEntityIsCombo(request, comboProductTran);
        transDetails.addAll(this.createComboProductTransDetailEntity(request, comboProductTran));
        transDetails.forEach(detail -> {
            comboProductTransDetailRepo.save(detail);
        });
        ComboProductTranDTO dto =  this.mapToOnlineOrderDTO(comboProductTran);
        return new Response<ComboProductTranDTO>().withData(dto);
    }

    @Override
    public Response<ComboProductTranDTO> getComboProductTrans(Long id) {
        ComboProductTrans comboProductTran = repository.findById(id)
                .orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_TRANS_NOT_EXISTS));

        ComboProductTranDTO dto = modelMapper.map(comboProductTran, ComboProductTranDTO.class);

        List<ComboProductTransDetail> transDetails = comboProductTransDetailRepo.findByTransId(id);
        List<ComboProductTransComboDTO> combos = new ArrayList<>();
        List<ComboProductTransProductDTO> products = new ArrayList<>();

        transDetails.forEach(detail -> {
            if(detail.getIsCombo() == 1) {
                ComboProductTransComboDTO combo = new ComboProductTransComboDTO();
                ComboProduct comboProduct = comboProductRepo.findById(detail.getComboProductId()).
                    orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_NOT_EXISTS));
                    combo.setProductCode(comboProduct.getProductCode());
                    combo.setProductName(comboProduct.getProductName());
                    combo.setQuantity(detail.getQuantity());
                    combo.setProductPrice(detail.getPrice());
                combos.add(combo);

                List<ComboProductTransDetail> productDetails =
                    transDetails.stream()
                                .filter(product -> product.getComboProductId().equals(detail.getComboProductId()))
                                .collect(Collectors.toList());

                productDetails.forEach(productDetail -> {
                    ComboProductTransProductDTO product = new ComboProductTransProductDTO();
                    Product productDB = productRepo.findById(productDetail.getProductId())
                        .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));

                    product.setProductCode(productDB.getProductCode());
                    product.setProductName(productDB.getProductName());
                    product.setComboProductCode(combo.getProductCode());
                    product.setFactor(productDetail.getQuantity()/combo.getQuantity());
                    product.setPrice(productDetail.getPrice());
                    product.setQuantity(productDetail.getQuantity());
                    products.add(product);
                });

            }

        });
        dto.setCombos(combos);
        dto.setProducts(products);
        return new Response<ComboProductTranDTO>().withData(dto);
    }

    // ComboProductTransDetail isCombo: 1
    private List<ComboProductTransDetail> createComboProductTransDetailEntityIsCombo(ComboProductTranRequest request, ComboProductTrans trans) {

        return request.getDetails().stream().map(combo -> {
            ComboProduct comboProduct = comboProductRepo.findById(combo.getComboProductId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_NOT_EXISTS));
            Price price = productPriceRepo.getByASCCustomerType(comboProduct.getRefProductId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.NO_PRICE_APPLIED));

            ComboProductTransDetail detail = new ComboProductTransDetail();
            detail.setTransId(trans.getId());
            detail.setShopId(trans.getShopId());
            detail.setTransCode(trans.getTransCode());
            detail.setTransDate(trans.getTransDate());
            detail.setComboProductId(combo.getComboProductId());
            detail.setProductId(comboProduct.getRefProductId());
            detail.setQuantity(combo.getQuantity());
            detail.setPrice(combo.getPrice());
            detail.setPriceNotVat(price.getPriceNotVat());
            detail.setIsCombo(1);
            detail.setAmount(combo.getPrice()*combo.getQuantity());

            return detail;
        }).collect(Collectors.toList());
    }

    //ComboProductTransDetail isCombo: 2
    private List<ComboProductTransDetail> createComboProductTransDetailEntity(ComboProductTranRequest request, ComboProductTrans trans) {
        List<ComboProductTransDetail> transDetails = new ArrayList<>();
        List<ComboProductTranDetailRequest> combos = request.getDetails();

        combos.forEach(combo -> {
            ComboProduct comboProduct = comboProductRepo.findById(combo.getComboProductId())
                    .orElseThrow(() -> new ValidateException(ResponseMessage.COMBO_PRODUCT_NOT_EXISTS));
            List<ComboProductDetail> comboProductDetails = comboProductDetailRepo.findByComboProductIdAndStatus(comboProduct.getId(), 1);
            comboProductDetails.forEach(comboProductDetail -> {
                Price price = productPriceRepo.getByASCCustomerType(comboProductDetail.getProductId())
                        .orElseThrow(() -> new ValidateException(ResponseMessage.NO_PRICE_APPLIED));
                ComboProductTransDetail detail = new ComboProductTransDetail();
                detail.setTransId(trans.getId());
                detail.setShopId(trans.getShopId());
                detail.setTransCode(trans.getTransCode());
                detail.setTransDate(trans.getTransDate());
                detail.setComboProductId(combo.getComboProductId());
                detail.setProductId(comboProductDetail.getProductId());
                detail.setQuantity(combo.getQuantity()*comboProductDetail.getFactor());
                detail.setPrice(price.getPrice());
                detail.setPriceNotVat(price.getPriceNotVat());
                detail.setIsCombo(2);
                detail.setAmount(detail.getPrice()*detail.getQuantity());
                transDetails.add(detail);
            });
        });

        return transDetails;
    }

    private ComboProductTrans createComboProductTransEntity(ComboProductTranRequest request, Long shopId, Long userId) {
        int totalQuantity = 0;
        float totalAmount = 0;
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        UserDTO user = userClient.getUserByIdV1(userId);
        ComboProductTrans comboProductTrans = new ComboProductTrans();
        comboProductTrans.setShopId(shopId);
        comboProductTrans.setTransCode(this.createComboProductTranCode(shopId, request));
        comboProductTrans.setTransDate(request.getTransDate());
        comboProductTrans.setTransType(request.getTransType());
        comboProductTrans.setNote(request.getNote());
        comboProductTrans.setWareHouseTypeId(customerTypeDTO.getWareHoseTypeId());
        List<ComboProductTranDetailRequest> combos = request.getDetails();
        for(ComboProductTranDetailRequest combo: combos) {
            totalQuantity += combo.getQuantity();
            totalAmount += (combo.getQuantity()*combo.getPrice());
        }
        comboProductTrans.setTotalQuantity(totalQuantity);
        comboProductTrans.setTotalAmount(totalAmount);
        comboProductTrans.setCreateUser(user.getUserAccount());

        return comboProductTrans;
    }


    private String createComboProductTranCode(Long shopId, ComboProductTranRequest request) {
        String transCode = null;
        String startWith = "";
        Integer comboNumber = 0;
        if(request.getTransType() == 1) {
            transCode = repository.getTransCodeTop1(shopId, "ICB");
            startWith = "ICB";
        }
        if(request.getTransType() == 2) {
            transCode = repository.getTransCodeTop1(shopId, "ECB");
            startWith = "ECB";
        }

        if(transCode!= null) {
            int i = transCode.lastIndexOf('.');
            String numberString = transCode.substring(i+1).trim();
            comboNumber = Integer.valueOf(numberString);
        }

        return this.createComboProductTranCode(shopId, comboNumber, startWith);
    }

    private String createComboProductTranCode(Long shopId, Integer comboNumber, String startWith) {
        LocalDate currentDate = LocalDate.now();
        Integer yy = currentDate.getYear();
        Integer mm = currentDate.getMonthValue();
        Integer dd = currentDate.getDayOfMonth();

        StringBuilder comboCode = new StringBuilder();
        comboCode.append(startWith + ".");
        comboCode.append(shopClient.getByIdV1(shopId).getData().getShopCode()+".");
        comboCode.append(yy);
        comboCode.append(Integer.toString(mm + 100).substring(1));
        comboCode.append(Integer.toString(dd + 100).substring(1) + ".");
        comboCode.append(Integer.toString(comboNumber + 10001).substring(1));

        return comboCode.toString();
    }

    private ComboProductTranDTO mapToOnlineOrderDTO(ComboProductTrans tran) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ComboProductTranDTO dto = modelMapper.map(tran, ComboProductTranDTO.class);
        return dto;
    }

}
