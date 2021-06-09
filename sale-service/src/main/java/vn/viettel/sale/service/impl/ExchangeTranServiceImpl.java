package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.UserDTO;
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
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.feign.CategoryDataClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ExchangeTransSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Response<List<CategoryDataDTO>> getReasons() {
        return categoryDataClient.getByCategoryGroupCodeV1();
    }

    @Override
    public CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> getAllExchange(Long roleId, Long shopId, String transCode, Date fromDate,
                                                                                  Date toDate, Long reasonId, Pageable pageable) {

        Long reason;
        if (reasonId == null)
            reason = null;
        else
            reason = (reasonId != null && reasonId == 7) ? null : reasonId;

        Page<ExchangeTrans> exchangeTransList = repository.findAll(Specification.where(ExchangeTransSpecification.hasTranCode(transCode))
                .and(ExchangeTransSpecification.hasFromDateToDate(fromDate, toDate))
                .and(ExchangeTransSpecification.hasStatus())
                .and(ExchangeTransSpecification.hasShopId(shopId))
                .and(ExchangeTransSpecification.hasReasonId(reason))
                .and(ExchangeTransSpecification.hasDetail())
                , pageable);

        List<ExchangeTransDTO> listResult = new ArrayList<>();
        ExchangeTotalDTO info = new ExchangeTotalDTO(0, 0D);
        for (ExchangeTrans exchangeTran : exchangeTransList) {
            ExchangeTransDTO exchangeTransDTO = mapExchangeToDTO(exchangeTran);
            if (exchangeTransDTO != null) {
                listResult.add(exchangeTransDTO);

                info.setTotalQuantity(info.getTotalQuantity() + exchangeTransDTO.getQuantity());
                info.setTotalAmount(info.getTotalAmount() + exchangeTransDTO.getTotalAmount());
            }
        }

        Page<ExchangeTransDTO> pageResult = new PageImpl<>(listResult, pageable, exchangeTransList.getTotalElements());
        return new CoverResponse<>(pageResult, info);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage create(ExchangeTransRequest request,Long userId,Long shopId) {
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO cusType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        List<CategoryDataDTO> cats = categoryDataClient.getByCategoryGroupCodeV1().getData();
        List<Long> catIds = cats.stream().map(CategoryDataDTO::getId).collect(Collectors.toList());
        if(!catIds.contains(request.getReasonId())){
            throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
        }
        if(cusType==null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        if(request.getCustomerId()==null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExchangeTrans exchangeTransRecord = modelMapper.map(request,ExchangeTrans.class);
        exchangeTransRecord.setTransCode(request.getTransCode());
        LocalDateTime date = LocalDateTime.now();
        exchangeTransRecord.setTransDate(date);
        exchangeTransRecord.setShopId(shopId);
        exchangeTransRecord.setStatus(1);
        exchangeTransRecord.setCreatedBy(user.getUserAccount());
        exchangeTransRecord.setWareHouseTypeId(cusType.getWareHouseTypeId());
        repository.save(exchangeTransRecord);
        for (ExchangeTransDetailRequest etd : request.getLstExchangeDetail()){
            if(etd.getQuantity().toString().length()>7) throw new ValidateException(ResponseMessage.QUANTITY_INVALID_STRING_LENGTH);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ExchangeTransDetail exchangeTransDetail = modelMapper.map(etd,ExchangeTransDetail.class);
            exchangeTransDetail.setTransId(exchangeTransRecord.getId());
            exchangeTransDetail.setTransDate(date);
            Price price = priceRepository.getByASCCustomerType(etd.getProductId()).get();
            exchangeTransDetail.setPrice(price.getPrice());
            exchangeTransDetail.setPriceNotVat(price.getPriceNotVat());
            exchangeTransDetail.setShopId(shopId);
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(etd.getProductId(),cusType.getWareHouseTypeId());
            if(stockTotal==null)
                throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
            stockTotal.setQuantity(stockTotal.getQuantity()-etd.getQuantity());
            if (stockTotal.getQuantity()<0)
                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
            stockTotal.setUpdatedBy(user.getUserAccount());
            transDetailRepository.save(exchangeTransDetail);
            stockTotalRepository.save(stockTotal);
        }
        return ResponseMessage.CREATED_SUCCESSFUL;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage update( Long id,ExchangeTransRequest request,Long shopId) {
        LocalDateTime date = LocalDateTime.now();
        ExchangeTrans exchange = repository.findById(id).get();
        if (DateUtils.formatDate2StringDate(exchange.getTransDate()).equals(DateUtils.formatDate2StringDate(date))) {
            exchange.setCustomerId(request.getCustomerId());
            exchange.setReasonId(request.getReasonId());
            repository.save(exchange);
            for(ExchangeTransDetailRequest a : request.getLstExchangeDetail()){

                /** delete record*/
                if(a.getType() == 2) {
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(a.getProductId(),exchange.getWareHouseTypeId());
                    stockTotal.setQuantity(stockTotal.getQuantity()+a.getQuantity());
                    transDetailRepository.deleteById(a.getProductId());
                    stockTotalRepository.save(stockTotal);
                }
                /** create record*/
                if(a.getType()==0){
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(a.getProductId(),exchange.getWareHouseTypeId());
                    stockTotal.setQuantity(stockTotal.getQuantity()-a.getQuantity());
                    if (stockTotal.getQuantity()<0)
                        throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    ExchangeTransDetail exchangeDetail = modelMapper.map(a,ExchangeTransDetail.class);
                    exchangeDetail.setTransId(exchange.getId());
                    exchangeDetail.setShopId(shopId);
                    Price price = priceRepository.getByASCCustomerType(a.getProductId()).get();
                    exchangeDetail.setPrice(price.getPrice());
                    exchangeDetail.setPriceNotVat(price.getPriceNotVat());
                    exchangeDetail.setTransDate(date);
                    stockTotalRepository.save(stockTotal);
                    transDetailRepository.save(exchangeDetail);
                }
                /** update record*/
                if(a.getType() == 1){
                    ExchangeTransDetail exchangeDetail = transDetailRepository.findById(a.getId()).get();
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(a.getProductId(),exchange.getWareHouseTypeId());
                    stockTotal.setQuantity(stockTotal.getQuantity()-(a.getQuantity()-exchangeDetail.getQuantity()));
                    if(stockTotal.getQuantity()<0)
                        throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                    exchangeDetail.setQuantity(a.getQuantity());
                    transDetailRepository.save(exchangeDetail);
                }

            }
            return ResponseMessage.SUCCESSFUL;
        }else throw new ValidateException(ResponseMessage.EXPIRED_FOR_UPDATE);
    }

    @Override
    public ExchangeTransDTO getExchangeTrans(Long id) {
        Optional<ExchangeTrans> exchangeTrans = repository.findById(id);
        if(!exchangeTrans.isPresent()){
            throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_NOT_FOUND);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExchangeTransDTO exchangeTransDTO = modelMapper.map(exchangeTrans.get(),ExchangeTransDTO.class);
        exchangeTransDTO.setListProducts(getBrokenProducts(id));

        Response<CustomerDTO> customerDTOResponse = customerClient.getCustomerByIdV1(exchangeTransDTO.getCustomerId());
        if (customerDTOResponse.getData() != null) {
            CustomerDTO customerDTO = customerDTOResponse.getData();
            exchangeTransDTO.setCustomerName(customerDTO.getLastName() + " " + customerDTO.getFirstName());
            exchangeTransDTO.setCustomerAddress(customerDTO.getAddress());
            exchangeTransDTO.setCustomerPhone(customerDTO.getMobiPhone());
        }
        return exchangeTransDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage remove(Long id) {
        Optional<ExchangeTrans> exchangeTrans = repository.findById(id);
        if(!exchangeTrans.isPresent()){
            throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_NOT_FOUND);
        }
        List<ExchangeTransDetail> exchangeTransDetails = transDetailRepository.findByTransId(exchangeTrans.get().getId());
        for(ExchangeTransDetail e :exchangeTransDetails){
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(e.getProductId(),exchangeTrans.get().getWareHouseTypeId());
            if(stockTotal==null)
                throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
            stockTotal.setQuantity(stockTotal.getQuantity()+e.getQuantity());
            stockTotalRepository.save(stockTotal);
        }
        exchangeTrans.get().setStatus(-1);
        return ResponseMessage.DELETE_SUCCESSFUL;
    }

    @Override
    public List<ExchangeTransDetailRequest> getBrokenProducts(Long id) {
        List<ExchangeTransDetailRequest> response = new ArrayList<>();
        List<ExchangeTransDetail> details = transDetailRepository.findByTransId(id);
        for (ExchangeTransDetail detail : details) {
            Product product = productRepository.findByIdAndStatus(detail.getProductId(), 1);
            if(product == null) throw  new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
            ExchangeTransDetailRequest productDTO = new ExchangeTransDetailRequest();
            productDTO.setId(product.getId());
            productDTO.setProductCode(product.getProductCode());
            productDTO.setProductName(product.getProductName());
            double price = priceRepository.getByASCCustomerType(product.getId()).get().getPrice();
            productDTO.setPrice(price);
            productDTO.setQuantity(detail.getQuantity());
            productDTO.setTotalPrice(price*detail.getQuantity());

            response.add(productDTO);
        }
        return response;
    }

    public Response<CategoryDataDTO> getReasonById(Long id) {
        return categoryDataClient.getReasonByIdV1(id);
    }

    private ExchangeTransDTO mapExchangeToDTO(ExchangeTrans exchangeTrans) {
        List<ExchangeTransDetail> details = transDetailRepository.findByTransId(exchangeTrans.getId());

        ExchangeTransDTO result = null;
        if (!details.isEmpty()) {
            result = modelMapper.map(exchangeTrans, ExchangeTransDTO.class);

            int quantity = 0;
            double totalAmount = 0;
            for (ExchangeTransDetail detail : details) {
                quantity += detail.getQuantity();
                totalAmount += detail.getPrice()*detail.getQuantity();
            }
            Response<CategoryDataDTO> categoryDataDTO = getReasonById(exchangeTrans.getReasonId());
            String reason = null;
            if (categoryDataDTO.getData() != null)
                reason = getReasonById(exchangeTrans.getReasonId()).getData().getCategoryName();
            if (reason == null)
                throw new ValidateException(ResponseMessage.INVALID_REASON);
            result.setReason(reason);
            result.setQuantity(quantity);
            result.setTotalAmount(totalAmount);
        }
        return result;
    }
}
