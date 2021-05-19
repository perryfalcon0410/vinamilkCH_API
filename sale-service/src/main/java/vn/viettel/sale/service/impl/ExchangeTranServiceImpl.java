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
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.service.dto.ProductDataSearchDTO;
import vn.viettel.sale.service.feign.CategoryDataClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ExchangeTransSpecification;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

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
        List<CategoryDataDTO> reasons = categoryDataClient.getByCategoryGroupCodeV1();
        return new Response<List<CategoryDataDTO>>().withData(reasons);
    }

    @Override
    public Response<Page<ExchangeTransDTO>> getAllExchange(Long roleId, Long shopId, String transCode, Date fromDate,
                                                           Date toDate, Long reasonId, Pageable pageable) {
        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        Page<ExchangeTrans> exchangeTransList = repository.findAll(Specification
                .where(ExchangeTransSpecification.hasTranCode(transCode))
                .and(ExchangeTransSpecification.hasFromDateToDate(fromDate, toDate))
                .and(ExchangeTransSpecification.hasReasonId(reasonId)), pageable);

        List<ExchangeTransDTO> listResult = new ArrayList<>();
        exchangeTransList.forEach(exchangeTrans -> {
            listResult.add(mapExchangeToDTO(exchangeTrans));
        });

        Page<ExchangeTransDTO> pageResult = new PageImpl<>(listResult);
        return new Response<Page<ExchangeTransDTO>>().withData(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<ExchangeTrans> create(ExchangeTransRequest request,Long userId,Long shopId) {
        Date date = new Date();
        Timestamp ts =new Timestamp(date.getTime());
        UserDTO user = userClient.getUserByIdV1(userId);
        CustomerTypeDTO cusType = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(cusType==null) throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExchangeTrans exchangeTransRecord = modelMapper.map(request,ExchangeTrans.class);
        exchangeTransRecord.setTransCode(request.getTransCode());
        exchangeTransRecord.setTransDate(date);
        exchangeTransRecord.setShopId(shopId);
        exchangeTransRecord.setWareHouseTypeId(cusType.getWareHouseTypeId());
        exchangeTransRecord.setCreateUser(user.getUserAccount());
        exchangeTransRecord.setCreatedAt(ts);
        repository.save(exchangeTransRecord);
        for (ExchangeTransDetailRequest etd : request.getLstExchangeDetail()){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ExchangeTransDetail exchangeTransDetail = modelMapper.map(etd,ExchangeTransDetail.class);
            exchangeTransDetail.setTransId(exchangeTransRecord.getId());
            Price price = priceRepository.getByASCCustomerType(etd.getProductId()).get();
            exchangeTransDetail.setPrice(price.getPrice());
            exchangeTransDetail.setPriceNotVat(price.getPriceNotVat());
            exchangeTransDetail.setShopId(shopId);
            exchangeTransDetail.setCreatedAt(ts);
            StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(etd.getProductId(),cusType.getWareHouseTypeId());
            if(stockTotal==null)
                throw new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND);
            stockTotal.setQuantity(stockTotal.getQuantity()-etd.getQuantity());
            if (stockTotal.getQuantity()<0)
                throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
            transDetailRepository.save(exchangeTransDetail);
            stockTotalRepository.save(stockTotal);
        }
        Response<ExchangeTrans> response = new Response<>();
        return response.withData(exchangeTransRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<String> update( Long id,ExchangeTransRequest request,Long shopId) {
        Date date = new Date();
        ExchangeTrans exchange = repository.findById(id).get();
        if (formatDate(exchange.getTransDate()).equals(formatDate(date))) {
            exchange.setCustomerId(request.getCustomerId());
            exchange.setReasonId(request.getReasonId());
            repository.save(exchange);
            for(ExchangeTransDetailRequest a : request.getLstExchangeDetail()){
                /** validation */
                if(a.getQuantity()<0 && a.getId() == -1){
                    throw new ValidateException(ResponseMessage.DO_NOT_CHEAT_DATABASE);
                }
                if(a.getQuantity()== null || a.getId() == null){
                    throw new ValidateException(ResponseMessage.DO_NOT_CHEAT_DATABASE);
                }
                /** delete record*/
                if(a.getQuantity() <0 && a.getId() != -1 )
                {
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(a.getProductId(),exchange.getWareHouseTypeId());
                    stockTotal.setQuantity(stockTotal.getQuantity()+a.getQuantity());
                    transDetailRepository.deleteById(a.getProductId());
                    stockTotalRepository.save(stockTotal);
                }
                /** create record*/
                if(a.getQuantity()>0 && a.getId() == -1){
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
                if(a.getQuantity()>0 && a.getId() != -1){
                    ExchangeTransDetail exchangeDetail = transDetailRepository.findById(a.getId()).get();
                    StockTotal stockTotal = stockTotalRepository.findByProductIdAndWareHouseTypeId(a.getProductId(),exchange.getWareHouseTypeId());
                    stockTotal.setQuantity(stockTotal.getQuantity()-(a.getQuantity()-exchangeDetail.getQuantity()));
                    if(stockTotal.getQuantity()<0)
                        throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
                    exchangeDetail.setQuantity(a.getQuantity());
                    transDetailRepository.save(exchangeDetail);
                }

            }
            return new Response<String>().withData(ResponseMessage.SUCCESSFUL.toString());
        }
        return new Response<String>().withData(ResponseMessage.UPDATE_FAILED.toString());
    }

    @Override
    public Response<ExchangeTransDTO> getExchangeTrans(Long id) {
        Optional<ExchangeTrans> exchangeTrans = repository.findById(id);
        if(!exchangeTrans.isPresent()){
            throw new ValidateException(ResponseMessage.EXCHANGE_TRANS_NOT_FOUND);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExchangeTransDTO exchangeTransDTO = modelMapper.map(exchangeTrans.get(),ExchangeTransDTO.class);
        return new Response<ExchangeTransDTO>().withData(exchangeTransDTO);
    }
    @Override
    public Response<List<ExchangeTransDetailRequest>> getBrokenProducts(Long id) {
        List<ExchangeTransDetailRequest> response = new ArrayList<>();
        List<ExchangeTransDetail> details = transDetailRepository.findByTransId(id);

        for (ExchangeTransDetail detail : details) {
            Product product = productRepository.findByIdAndStatus(id, 1);
            ExchangeTransDetailRequest productDTO = new ExchangeTransDetailRequest();

            productDTO.setId(product.getId());
            productDTO.setProductCode(product.getProductCode());
            productDTO.setProductName(product.getProductName());
            float price = priceRepository.getByASCCustomerType(product.getId()).get().getPrice();
            productDTO.setPrice(price);
            productDTO.setQuantity(detail.getQuantity());
            productDTO.setTotalPrice(price*detail.getQuantity());

            response.add(productDTO);
        }
        return new Response<List<ExchangeTransDetailRequest>>().withData(response);
    }

    public CategoryDataDTO getReasonById(Long id) {
        return categoryDataClient.getReasonByIdV1(id);
    }

    private ExchangeTransDTO mapExchangeToDTO(ExchangeTrans exchangeTrans) {
        List<ExchangeTransDetail> details = transDetailRepository.findByTransId(exchangeTrans.getId());

        ExchangeTransDTO result = modelMapper.map(exchangeTrans, ExchangeTransDTO.class);

        if (!details.isEmpty()) {
            int quantity = 0;
            float totalAmount = 0;
            for (ExchangeTransDetail detail : details) {
                quantity += detail.getQuantity();
                totalAmount += detail.getPrice()*detail.getQuantity();
            }
            String reason = getReasonById(exchangeTrans.getReasonId()).getCategoryName();
            if (reason == null)
                throw new ValidateException(ResponseMessage.INVALID_REASON);
            result.setReason(reason);
            result.setQuantity(quantity);
            result.setTotalAmount(totalAmount);
        }
        return result;
    }
}
