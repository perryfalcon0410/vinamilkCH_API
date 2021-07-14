package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.feign.CategoryDataClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ExchangeTransSpecification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExchangeTranServiceImpl extends BaseServiceImpl<ExchangeTrans, ExchangeTransRepository> implements ExchangeTranService {
    @Autowired
    CategoryDataClient categoryDataClient;
    @Autowired
    ExchangeTransDetailRepository transDetailRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    CustomerClient customerClient;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductPriceRepository priceRepository;
    @Autowired
    CustomerTypeClient customerTypeClient;
    @Autowired
    StockTotalRepository stockTotalRepository;

    @Autowired
    StockTotalService stockTotalService;

    @Override
    public Response<List<CategoryDataDTO>> getReasons() {
        return categoryDataClient.getByCategoryGroupCodeV1();
    }

    @Override
    public CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> getAllExchange(Long roleId, Long shopId, String transCode, Date fromDate,
                                                                                  Date toDate, Long reasonId, Pageable pageable) {
        if (transCode != null) transCode = transCode.trim().toUpperCase();
        Optional<Sort.Order> order = pageable.getSort().stream().findFirst();
        if (!order.isPresent()) pageable.getSort().and(Sort.by("transDate").descending());
        Page<ExchangeTrans> exchangeTransList = repository.findAll(Specification.where(ExchangeTransSpecification.hasTranCode(transCode))
                        .and(ExchangeTransSpecification.hasFromDateToDate(fromDate, toDate))
                        .and(ExchangeTransSpecification.hasStatus())
                        .and(ExchangeTransSpecification.hasShopId(shopId))
                        .and(ExchangeTransSpecification.hasReasonId(reasonId))
                , pageable);
        List<CategoryDataDTO> reasonExchanges = categoryDataClient.getReasonExchangeV1().getData();
        Page<ExchangeTransDTO> pageResult = exchangeTransList.map(item -> mapExchangeToDTO(item, reasonExchanges));
        ExchangeTotalDTO exchangeTotalDTO = repository.getExchangeTotal(shopId, transCode, 1, reasonId,
                DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
        return new CoverResponse<>(pageResult, exchangeTotalDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage create(ExchangeTransRequest request, Long userId, Long shopId) {
        CustomerTypeDTO cusType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        List<CategoryDataDTO> cats = categoryDataClient.getByCategoryGroupCodeV1().getData();
        List<Long> catIds = cats.stream().map(CategoryDataDTO::getId).collect(Collectors.toList());
        List<String> exChangeCodes = repository.getListExChangeCodes();
        if (exChangeCodes.contains(request.getTransCode()))
            throw new ValidateException(ResponseMessage.EXCHANGE_CODE_IS_EXIST);
        if (!catIds.contains(request.getReasonId())) throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
        if (cusType == null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        if (request.getCustomerId() == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        LocalDateTime date = LocalDateTime.now();
        List<Long> productIds = request.getLstExchangeDetail().stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());
        List<Price> prices = priceRepository.findProductPrice(productIds, cusType.getId(), date);

        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, cusType.getWareHouseTypeId(), productIds);
        stockTotalService.lockUnLockRecord(stockTotals, true);

        validate(request.getLstExchangeDetail(), productIds, prices, stockTotals, null);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExchangeTrans exchangeTransRecord = modelMapper.map(request, ExchangeTrans.class);
        exchangeTransRecord.setTransCode(request.getTransCode());
        exchangeTransRecord.setTransDate(date);
        exchangeTransRecord.setShopId(shopId);
        exchangeTransRecord.setStatus(1);
        exchangeTransRecord.setWareHouseTypeId(cusType.getWareHouseTypeId());
        repository.save(exchangeTransRecord);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        for (ExchangeTransDetailRequest etd : request.getLstExchangeDetail()) {
            ExchangeTransDetail exchangeTransDetail = modelMapper.map(etd, ExchangeTransDetail.class);
            exchangeTransDetail.setTransId(exchangeTransRecord.getId());
            exchangeTransDetail.setTransDate(date);
            setPrice(prices, exchangeTransDetail);
            exchangeTransDetail.setShopId(shopId);
            StockTotal stockTotal = getStockTotal(stockTotals, etd.getProductId());
            stockTotal.setQuantity(stockTotal.getQuantity() - etd.getQuantity());
            transDetailRepository.save(exchangeTransDetail);
            stockTotalRepository.save(stockTotal);
        }
        stockTotalService.lockUnLockRecord(stockTotals, false);
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    private void validate(List<ExchangeTransDetailRequest> details, List<Long> productIds, List<Price> prices, List<StockTotal> stockTotals,
                          List<ExchangeTransDetail> dbExchangeTransDetails) {
        if (details == null || productIds == null) return;
        if ((prices != null && productIds.size() != prices.size()) || (stockTotals != null && productIds.size() != stockTotals.size())) {
            List<Long> productIds1 = prices == null ? null : prices.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
            List<Long> productIds2 = stockTotals == null ? null : stockTotals.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
            for (ExchangeTransDetailRequest item : details) {
                if (productIds1 != null && !productIds1.contains(item.getProductId())) {
                    throw new ValidateException(ResponseMessage.PRODUCT_PRICE_NOT_FOUND, item.getProductCode() + " - " + item.getProductName());
                }
                if (productIds2 != null && !productIds2.contains(item.getProductId())) {
                    throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, item.getProductCode() + " - " + item.getProductName());
                }
            }
        }
        //kiểm tra tồn kho có đủ
        List<Long> exchangeTransIds = null;
        if (dbExchangeTransDetails != null && !dbExchangeTransDetails.isEmpty())
            exchangeTransIds = dbExchangeTransDetails.stream().map(item ->
                    item.getId()).collect(Collectors.toList());
        for (ExchangeTransDetailRequest item : details) {
            if (item.getQuantity() == null || item.getQuantity() == 0) {
                throw new ValidateException(ResponseMessage.NUMBER_GREATER_THAN_ZERO);
            }
            Integer qty = null;
            /** create record*/
            if (exchangeTransIds == null) {
                qty = item.getQuantity();
            }
            /** update record*/
            else if (exchangeTransIds.contains(item.getId()) && item.getType() != 2) {
                for (ExchangeTransDetail exchangeDetail : dbExchangeTransDetails) {
                    if (exchangeDetail.getId().equals(item.getId())) {
                        qty = item.getQuantity() - exchangeDetail.getQuantity();
                        break;
                    }
                }
            }
            if (qty != null) {
                for (StockTotal stockTotal : stockTotals) {
                    if (stockTotal.getProductId().equals(item.getProductId()) && item.getQuantity() > stockTotal.getQuantity()) {
                        throw new ValidateException(ResponseMessage.STOCK_TOTAL_LESS_THAN, item.getProductCode() + " - " + item.getProductName(), stockTotal.getQuantity() + "");
                    }
                }
            }
        }
    }

    private void setPrice(List<Price> prices, ExchangeTransDetail exchangeTransDetail) {
        if (prices != null) {
            for (Price price : prices) {
                if (price.getProductId().equals(exchangeTransDetail.getProductId())) {
                    exchangeTransDetail.setPrice(price.getPrice());
                    exchangeTransDetail.setPriceNotVat(price.getPriceNotVat());
                    break;
                }
            }
        }
    }

    private StockTotal getStockTotal(List<StockTotal> stockTotals, Long productId) {
        StockTotal stockTotal = null;
        if (stockTotals != null) {
            for (StockTotal stockTotal1 : stockTotals) {
                if (stockTotal1.getProductId().equals(productId)) {
                    stockTotal = stockTotal1;
                    break;
                }
            }
        }
        if (stockTotal.getQuantity() == null) stockTotal.setQuantity(0);
        return stockTotal;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage update(Long exchangeTranId, ExchangeTransRequest request, Long shopId) {
        LocalDateTime date = LocalDateTime.now();
        ExchangeTrans exchange = repository.findById(exchangeTranId).get();
        if (DateUtils.formatDate2StringDate(exchange.getTransDate()).equals(DateUtils.formatDate2StringDate(date))) {
            List<String> listTransCode = repository.getListExChangeCodes();
            if (listTransCode == null) throw new ValidateException(ResponseMessage.EXCHANGE_CODE_IS_EXIST);
            List<Long> productIds = request.getLstExchangeDetail().stream().map(
                    item -> item.getProductId()).distinct().collect(Collectors.toList());
            Long customerTypeId = null;
            CustomerDTO customerDTO = customerClient.getCustomerByIdV1(request.getCustomerId()).getData();
            if (customerDTO != null) customerTypeId = customerDTO.getCustomerTypeId();
            List<Price> prices = priceRepository.findProductPrice(productIds, customerTypeId, date);

            List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, exchange.getWareHouseTypeId(), productIds);
            stockTotalService.lockUnLockRecord(stockTotals, true);
            List<ExchangeTransDetail> dbExchangeTransDetails = transDetailRepository.findByTransId(exchangeTranId);
            validate(request.getLstExchangeDetail(), productIds, prices, stockTotals, dbExchangeTransDetails);

            listTransCode.remove(exchange.getTransCode());
            if (!listTransCode.contains(request.getTransCode())) exchange.setTransCode(request.getTransCode());
            exchange.setCustomerId(request.getCustomerId());
            exchange.setReasonId(request.getReasonId());
            repository.save(exchange);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            for (ExchangeTransDetailRequest req : request.getLstExchangeDetail()) {
                for (ExchangeTransDetail item : dbExchangeTransDetails) {
                        StockTotal stockTotal = getStockTotal(stockTotals, req.getProductId());
                        /** create record*/
                        if (req.getType() == 0 || req.getId() == null || req.getId() == 0) {
                            stockTotal.setQuantity(stockTotal.getQuantity() - req.getQuantity());
                            ExchangeTransDetail exchangeDetail = modelMapper.map(req, ExchangeTransDetail.class);
                            exchangeDetail.setTransId(exchange.getId());
                            exchangeDetail.setShopId(shopId);
                            setPrice(prices, exchangeDetail);
                            exchangeDetail.setTransDate(date);
                            stockTotalRepository.save(stockTotal);
                            transDetailRepository.save(exchangeDetail);
                        } else {
                                /** delete record*/
                                if (req.getType() == 2) {
                                    stockTotal.setQuantity(stockTotal.getQuantity() + req.getQuantity());
                                    transDetailRepository.deleteById(req.getId());
                                } else {/** update record*/
                                    stockTotal.setQuantity(stockTotal.getQuantity() - (req.getQuantity() - item.getQuantity()));
                                    item.setQuantity(req.getQuantity());
                                    transDetailRepository.save(item);
                                }
                                stockTotalRepository.save(stockTotal);
                                break;
                            }
                }
            }
            stockTotalService.lockUnLockRecord(stockTotals, false);
        } else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
        return ResponseMessage.UPDATE_SUCCESSFUL;
    }

    @Override
    public ExchangeTransDTO getExchangeTrans(Long id) {
        Optional<ExchangeTrans> exchangeTrans = repository.findById(id);
        if (!exchangeTrans.isPresent()) {
            throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_NOT_FOUND);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExchangeTransDTO exchangeTransDTO = modelMapper.map(exchangeTrans.get(), ExchangeTransDTO.class);
        exchangeTransDTO.setListProducts(getBrokenProducts(id));
        Response<CustomerDTO> customerDTOResponse = customerClient.getCustomerByIdV1(exchangeTransDTO.getCustomerId());
        if (customerDTOResponse.getData() != null) {
            CustomerDTO customerDTO = customerDTOResponse.getData();
            exchangeTransDTO.setCustomerName(customerDTO.getFullName());
            exchangeTransDTO.setCustomerAddress(customerDTO.getAddress());
            exchangeTransDTO.setCustomerPhone(customerDTO.getMobiPhone());
        }
        return exchangeTransDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage remove(Long id, Long shopId) {
        Optional<ExchangeTrans> exchangeTrans = repository.findById(id);
        if (!exchangeTrans.isPresent()) {
            throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_NOT_FOUND);
        }
        List<ExchangeTransDetail> exchangeTransDetails = transDetailRepository.findByTransId(exchangeTrans.get().getId());
        List<Long> productIds = exchangeTransDetails.stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());

        List<StockTotal> stockTotals = stockTotalRepository.getStockTotal(shopId, exchangeTrans.get().getWareHouseTypeId(), productIds);
        stockTotalService.lockUnLockRecord(stockTotals, true);
        if (stockTotals != null && productIds.size() != stockTotals.size()) {
            List<Long> productIds2 = stockTotals == null ? null : stockTotals.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
            List<Product> products = productRepository.getProducts(productIds, null);
            for (ExchangeTransDetail item : exchangeTransDetails) {
                if (productIds2 != null && !productIds2.contains(item.getProductId())) {
                    for (Product product : products) {
                        if (product.getId().equals(item.getProductId()))
                            throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, product.getProductCode() + " - " + product.getProductName());
                    }
                }
            }
        }
        for (ExchangeTransDetail e : exchangeTransDetails) {
            for (StockTotal stockTotal : stockTotals) {
                if (stockTotal.getProductId().equals(e.getProductId())) {
                    stockTotal.setQuantity(stockTotal.getQuantity() + e.getQuantity());
                    stockTotalRepository.save(stockTotal);
                    break;
                }
            }
        }
        stockTotalService.lockUnLockRecord(stockTotals, false);
        exchangeTrans.get().setStatus(-1);
        return ResponseMessage.DELETE_SUCCESSFUL;
    }

    @Override
    public List<ExchangeTransDetailRequest> getBrokenProducts(Long transId) {
        Optional<ExchangeTrans> exchangeTrans = repository.findById(transId);
        if (!exchangeTrans.isPresent()) {
            throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_NOT_FOUND);
        }
        List<ExchangeTransDetailRequest> response = new ArrayList<>();
        List<ExchangeTransDetail> details = transDetailRepository.findByTransId(transId);
//        Long customerTypeId = null;
//        CustomerDTO customerDTO = customerClient.getCustomerByIdV1(exchangeTrans.get().getCustomerId()).getData();
//        if(customerDTO != null) customerTypeId = customerDTO.getCustomerTypeId();
        List<Long> productIds = details.stream().map(item -> item.getProductId()).distinct().collect(Collectors.toList());
//        List<Price> prices = priceRepository.findProductPrice(productIds, customerTypeId, LocalDateTime.now());
        List<Product> products = productRepository.getProducts(productIds, null);

        for (ExchangeTransDetail detail : details) {
            ExchangeTransDetailRequest productDTO = new ExchangeTransDetailRequest();
            productDTO.setId(detail.getId());
            productDTO.setProductId(detail.getProductId());
            productDTO.setPrice(detail.getPrice());
            for (Product product : products) {
                if (product.getId().equals(detail.getProductId())) {
                    productDTO.setProductCode(product.getProductCode());
                    productDTO.setProductName(product.getProductName());
                    productDTO.setUnit(product.getUom1());
                }
            }
//            if(prices != null){
//                for(Price price : prices){
//                    if(price.getProductId().equals(detail.getProductId())){
//                        productDTO.setPrice(price.getPrice());
//                        break;
//                    }
//                }
//            }
            productDTO.setQuantity(detail.getQuantity());
            productDTO.setTotalPrice(productDTO.getPrice() * detail.getQuantity());

            response.add(productDTO);
        }
        return response;
    }

    private ExchangeTransDTO mapExchangeToDTO(ExchangeTrans exchangeTran, List<CategoryDataDTO> reasonExchanges) {
        ExchangeTransDTO result = modelMapper.map(exchangeTran, ExchangeTransDTO.class);
        if (reasonExchanges != null) {
            for (CategoryDataDTO reason : reasonExchanges) {
                if (reason.getId().equals(exchangeTran.getReasonId())) {
                    result.setReason(reason.getCategoryName());
                    break;
                }
            }
        }

        return result;
    }
}
