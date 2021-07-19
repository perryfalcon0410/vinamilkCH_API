package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ComboProductTranDetailRequest;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.ComboProductTransComboDTO;
import vn.viettel.sale.service.dto.ComboProductTransProductDTO;
import vn.viettel.sale.service.dto.TotalDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ComboProductTranSpecification;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    StockTotalRepository stockTotalRepo;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    UserClient userClient;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    StockTotalService stockTotalService;

    @Override
    public CoverResponse<Page<ComboProductTranDTO>, TotalDTO> getAll(ComboProductTranFilter filter, Pageable pageable) {

        Page<ComboProductTrans> comboProductTrans = repository.findAll(Specification.where(
                ComboProductTranSpecification.hasTransCode(filter.getTransCode())
                .and(ComboProductTranSpecification.hasTransType(filter.getTransType()))
                .and(ComboProductTranSpecification.hasShopId(filter.getShopId()))
                .and(ComboProductTranSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate()))
            ), pageable);

        Page<ComboProductTranDTO> pageProductTranDTOS = comboProductTrans.map(this::mapToOnlineOrderDTO);

        LocalDateTime fromDate = LocalDateTime.of(2015,1,1,0,0);
        LocalDateTime toDate = LocalDateTime.now();
        if (filter.getFromDate() != null) fromDate = filter.getFromDate();
        if (filter.getToDate() != null) toDate = filter.getToDate();
        fromDate = DateUtils.convertFromDate(fromDate);
        toDate = DateUtils.convertToDate(toDate);
        TotalDTO totalDTO = repository.getExchangeTotal(filter.getShopId(), filter.getTransCode(), filter.getTransType(),
                fromDate, toDate);

        CoverResponse coverResponse = new CoverResponse(pageProductTranDTOS, totalDTO);
        return coverResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ComboProductTranDTO create(ComboProductTranRequest request, Long shopId, String userName) {
        if(request.getDetails().isEmpty()) throw new ValidateException(ResponseMessage.COMBO_PRODUCT_LIST_MUST_BE_NOT_EMPTY);
        Long customerTypeId = null;
        CustomerDTO customerDTO = customerClient.getCusDefault(shopId);
        if(customerDTO != null) customerTypeId = customerDTO.getCustomerTypeId();

        Long warehouseTypeId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        if (warehouseTypeId == null)
            throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);


        ComboProductTrans comboProductTran = this.createComboProductTransEntity(request, warehouseTypeId, shopId, customerTypeId);

        List<ComboProductTransDetail> comboProducts = new ArrayList<>();
        List<ComboProductTranDetailRequest> combos = request.getDetails();
        List<ComboProductDetail> comboProductDetails = comboProductDetailRepo.getComboProductDetail(
                combos.stream().map(item -> item.getComboProductId()).distinct().collect(Collectors.toList()), 1);
        List<Long> lstProductIds = comboProductDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
        List<Price> prices = productPriceRepo.findProductPrice(lstProductIds, customerTypeId, LocalDateTime.now());
        List<StockTotal> lstSaveStockTotal = new ArrayList<>();
        List<Long> lstProductIds1 = request.getDetails().stream().map(item -> item.getRefProductId()).distinct().collect(Collectors.toList());
        List<Product> lstProducts = productRepo.getProducts(lstProductIds1, null);
        lstProductIds1.forEach(lstProductIds::add);
        lstProductIds.stream().distinct();
        List<StockTotal> stockTotals = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, lstProductIds);
        stockTotalService.lockUnLockRecord(stockTotals, true);

        combos.forEach(combo -> {
            StockTotal stockTotal1 = null;
            if(stockTotals != null){
                for(StockTotal st : stockTotals){
                    if(st.getProductId().equals(combo.getRefProductId())){
                        stockTotal1 = st;
                        break;
                    }
                }
            }
            //Combo cha chưa có tồn kho - tạo mới stock total
            if(stockTotal1 == null){
                stockTotal1 = new StockTotal();
                stockTotal1.setShopId(shopId);
                stockTotal1.setWareHouseTypeId(warehouseTypeId);
                stockTotal1.setProductId(combo.getRefProductId());
                stockTotal1.setStatus(1);
                stockTotal1 = stockTotalRepo.save(stockTotal1);
               //throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
            }

            int quatity1 = stockTotal1.getQuantity()!=null?stockTotal1.getQuantity():0;
            if(request.getTransType().equals(1)) {
                stockTotal1.setQuantity(quatity1 + combo.getQuantity());
            }else{
                if(quatity1 < combo.getQuantity()) {
                    if(lstProducts != null){
                        for(Product product : lstProducts){
                            if(product.getId().equals(combo.getRefProductId())){
                                throw new ValidateException(ResponseMessage.STOCK_TOTAL_LESS_THAN,
                                        product.getProductCode() + " - " + product.getProductName(), stockTotal1.getQuantity().toString());
                            }
                        }
                    }
                }
                stockTotal1.setQuantity(quatity1 - combo.getQuantity());
            }
            lstSaveStockTotal.add(stockTotal1);

            ComboProductTransDetail cbDetail = new ComboProductTransDetail();
            cbDetail.setTransId(comboProductTran.getId());
            cbDetail.setShopId(comboProductTran.getShopId());
            cbDetail.setTransCode(comboProductTran.getTransCode());
            cbDetail.setTransDate(comboProductTran.getTransDate());
            cbDetail.setComboProductId(combo.getComboProductId());
            cbDetail.setProductId(combo.getRefProductId());
            cbDetail.setQuantity(combo.getQuantity());
            cbDetail.setPrice(combo.getPrice());
            cbDetail.setPriceNotVat(combo.getPriceNotVAT());
            cbDetail.setIsCombo(1);
            cbDetail.setAmount(combo.getPrice()*combo.getQuantity());
            comboProducts.add(cbDetail);

            comboProductDetails.forEach(comboProductDetail -> {
                if(comboProductDetail.getFactor() == null || comboProductDetail.getFactor() < 1 )
                    throw new ValidateException(ResponseMessage.COMBO_PRODUCT_FACTOR_REJECT);

                Price productPrice = null;
                if(prices != null){
                    for(Price price : prices){
                        if(price.getProductId().equals(comboProductDetail.getProductId())){
                            productPrice = price;
                            break;
                        }
                    }
                }
                if(productPrice == null){
                    throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
                }
                StockTotal stockTotal = null;
                if(stockTotals != null){
                    for(StockTotal st : stockTotals){
                        if(st.getProductId().equals(comboProductDetail.getProductId())){
                            stockTotal = st;
                            break;
                        }
                    }
                }
                if(stockTotal == null){
                    throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
                }
                int quatity = stockTotal.getQuantity()!=null?stockTotal.getQuantity():0;

                // - stock total when type = 1 /+ stock total when type = 2
                if(request.getTransType().equals(1)) {
                    if(quatity < combo.getQuantity()) {
                        Product product = productRepo.findById(combo.getRefProductId()).get();
                        throw new ValidateException(ResponseMessage.STOCK_TOTAL_LESS_THAN,
                                product.getProductCode() + " - " + product.getProductName(), stockTotal.getQuantity().toString());
                    }
                    stockTotal.setQuantity(quatity - (combo.getQuantity()*comboProductDetail.getFactor()));
                }else{
                    stockTotal.setQuantity(quatity +(combo.getQuantity()*comboProductDetail.getFactor()));
                }
                lstSaveStockTotal.add(stockTotal);

                double price = productPrice.getPrice()!=null?productPrice.getPrice():0;
                ComboProductTransDetail detail = new ComboProductTransDetail();
                detail.setTransId(comboProductTran.getId());
                detail.setShopId(comboProductTran.getShopId());
                detail.setTransCode(comboProductTran.getTransCode());
                detail.setTransDate(comboProductTran.getTransDate());
                detail.setComboProductId(combo.getComboProductId());
                detail.setProductId(comboProductDetail.getProductId());
                detail.setQuantity(combo.getQuantity()*comboProductDetail.getFactor());
                detail.setPrice(Double.valueOf(price));
                detail.setPriceNotVat(Double.valueOf(productPrice.getPriceNotVat()));
                detail.setIsCombo(2);
                detail.setAmount(detail.getPrice()*detail.getQuantity());
                comboProducts.add(detail);
            });
        });

        try {
            repository.save(comboProductTran);
        }catch(Exception e) {
            throw new ValidateException(ResponseMessage.CREATE_COMBO_PRODUCT_TRANS_FAIL);
        }
        comboProducts.forEach(detail -> {detail.setTransId(comboProductTran.getId()); comboProductTransDetailRepo.save(detail); });
        lstSaveStockTotal.forEach(detail -> stockTotalRepo.save(detail));
        stockTotalService.lockUnLockRecord(stockTotals, false);
       return this.mapToOnlineOrderDTO(comboProductTran);
    }

    @Override
    public ComboProductTranDTO getComboProductTrans(Long id) {
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

                List<ComboProductTransDetail> productDetails = transDetails.stream()
                    .filter(product -> product.getComboProductId().equals(detail.getComboProductId()) && product.getIsCombo() == 2)
                    .collect(Collectors.toList());

                List<Long> lstProductIds = productDetails.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
                List<Product> lstProducts = productRepo.getProducts(lstProductIds, null);
                productDetails.forEach(productDetail -> {
                    ComboProductTransProductDTO product = new ComboProductTransProductDTO();
                    if(lstProducts != null){
                        for(Product productDB : lstProducts){
                            if(productDB.getId().equals(productDetail.getProductId())){
                                product.setProductCode(productDB.getProductCode());
                                product.setProductName(productDB.getProductName());
                                break;
                            }
                        }
                    }
                    product.setComboProductCode(combo.getProductCode());
                    product.setFactor(productDetail.getQuantity()/combo.getQuantity());
                    product.setPrice(productDetail.getPrice());
                    product.setQuantity(productDetail.getQuantity());
                    dto.addProductTotals(productDetail.getQuantity());
                    products.add(product);
                });
            }

        });
        dto.setCombos(combos);
        dto.setProducts(products);
        return dto;
    }

    private ComboProductTrans createComboProductTransEntity(ComboProductTranRequest request, Long wareHoseTypeId, Long shopId, Long customerTypeId) {
        int totalQuantity = 0;
        double totalAmount = 0;

        ComboProductTrans comboProductTrans = new ComboProductTrans();
        comboProductTrans.setShopId(shopId);
        comboProductTrans.setTransCode(this.createComboProductTranCode(shopId, request));
        comboProductTrans.setTransDate(request.getTransDate());
        comboProductTrans.setTransType(request.getTransType());
        comboProductTrans.setNote(request.getNote());
        comboProductTrans.setWareHouseTypeId(wareHoseTypeId);
        List<ComboProductTranDetailRequest> combos = request.getDetails();
        List<ComboProduct> comboProducts = comboProductRepo.findAllById(combos.stream().map(item -> item.getComboProductId()).collect(Collectors.toList()));
        List<Long> lstProductIds = comboProducts.stream().map(item -> item.getRefProductId()).distinct().collect(Collectors.toList());
        List<Price> prices = productPriceRepo.findProductPrice(lstProductIds, customerTypeId, LocalDateTime.now());
        for(ComboProductTranDetailRequest combo: combos) {
            if(combo.getPrice()!=null && combo.getPrice() <= 0 ) throw new ValidateException(ResponseMessage.PRICE_REJECT);
            ComboProduct comboProduct = null;
            if(comboProducts != null) {
                for (ComboProduct product : comboProducts) {
                    if (product.getId().equals(combo.getComboProductId())) {
                        comboProduct = product;
                        break;
                    }
                }
            }
            if(comboProduct == null){
                throw new ValidateException(ResponseMessage.COMBO_PRODUCT_NOT_EXISTS);
            }
            Price productPrice = null;
            if(prices != null){
                for(Price price : prices){
                    if(price.getProductId().equals(comboProduct.getRefProductId())){
                        productPrice = price;
                        break;
                    }
                }
            }

            if(productPrice == null){
                throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);
            }

            Double price = combo.getPrice()!=null?combo.getPrice(): productPrice.getPrice();
            Double priceNotVAT = Double.valueOf(productPrice.getPriceNotVat());
            if(!price.equals(productPrice.getPrice())) {
                double vat = productPrice.getVat()!=null?productPrice.getVat():0;
                priceNotVAT = price/(1+vat/100);
            }

            combo.setPrice(price);
            combo.setPriceNotVAT(priceNotVAT);
            combo.setRefProductId(comboProduct.getRefProductId());

            totalQuantity += combo.getQuantity();
            totalAmount += (combo.getQuantity()*combo.getPrice());
        }
        comboProductTrans.setTotalQuantity(totalQuantity);
        comboProductTrans.setTotalAmount(totalAmount);
        return comboProductTrans;
    }

    private String createComboProductTranCode(Long shopId, ComboProductTranRequest request) {
        String transCode = null;
        String startWith = "";
        Integer comboNumber = 0;
        if(request.getTransType() == 1) {
            startWith = this.createComboProductTranCode(shopId, "ICB");
        }
        if(request.getTransType() == 2) {
            startWith = this.createComboProductTranCode(shopId, "ECB");
        }
        List<String> codes = repository.getTransCodeTop1(shopId, startWith);
        if(codes != null && !codes.isEmpty()) transCode = codes.get(0);

        if(transCode!= null) {
            int i = transCode.lastIndexOf('.');
            String numberString = transCode.substring(i+1).trim();
            comboNumber = Integer.valueOf(numberString);
            return  startWith + Integer.toString(comboNumber + 10001).substring(1);
        }

        return startWith + "0001";
    }

    private String createComboProductTranCode(Long shopId, String startWith) {
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

        return comboCode.toString();
    }

    private ComboProductTranDTO mapToOnlineOrderDTO(ComboProductTrans tran) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ComboProductTranDTO dto = modelMapper.map(tran, ComboProductTranDTO.class);
        return dto;
    }

}
