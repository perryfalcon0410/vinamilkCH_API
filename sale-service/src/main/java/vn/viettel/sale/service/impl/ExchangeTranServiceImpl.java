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
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.entities.ExchangeTransDetail;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.repository.ExchangeTransDetailRepository;
import vn.viettel.sale.repository.ExchangeTransRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeProductDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.feign.CategoryDataClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.UserClient;
import vn.viettel.sale.specification.ExchangeTransSpecification;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public Response<ExchangeTrans> create(ExchangeTransRequest request,Long userId) {
        Date date = new Date();
        Timestamp ts =new Timestamp(date.getTime());

        UserDTO user = userClient.getUserByIdV1(userId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExchangeTrans exchangeTransRecord = modelMapper.map(request,ExchangeTrans.class);
        exchangeTransRecord.setTransDate(date);
        exchangeTransRecord.setCreateUser(user.getUserAccount());
        exchangeTransRecord.setCreatedAt(ts);
        Response<CustomerDTO> cus = customerClient.getCustomerByIdV1(request.getCustomerId());
        if(cus==null){
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        }
        CategoryDataDTO reason = categoryDataClient.getReasonByIdV1(request.getReasonId());
        if(reason == null){
            throw new ValidateException(ResponseMessage.REASON_NOT_FOUND);
        }
        /*
        Miss total quantity
        Miss total amount
        Miss cus address
        Miss cus phone
        */
        repository.save(exchangeTransRecord);
        List<ExchangeTransDetail> list1 = request.getLstExchangeDetail().stream().map(
                item -> modelMapper.map(item, ExchangeTransDetail.class)
        ).collect(Collectors.toList());
        for (ExchangeTransDetail etd : list1){

            etd.setTransId(exchangeTransRecord.getId());
            etd.setCreatedAt(ts);
            transDetailRepository.save(etd);
        }
        Response<ExchangeTrans> response = new Response<>();
        return response.withData(exchangeTransRecord);
    }

    @Override
    public Response<List<ExchangeProductDTO>> getBrokenProducts(Long id) {
        List<ExchangeProductDTO> response = new ArrayList<>();
        List<ExchangeTransDetail> details = transDetailRepository.findByTransId(id);

        for (ExchangeTransDetail detail : details) {
            Product product = productRepository.findByIdAndStatus(id, 1);
            ExchangeProductDTO productDTO = new ExchangeProductDTO();

            productDTO.setId(product.getId());
            productDTO.setProductCode(product.getProductCode());
            productDTO.setProductName(product.getProductName());
            float price = priceRepository.getByASCCustomerType(product.getId()).get().getPrice();
            productDTO.setPrice(price);
            productDTO.setQuantity(detail.getQuantity());
            productDTO.setTotalAmount(price*detail.getQuantity());

            response.add(productDTO);
        }
        return new Response<List<ExchangeProductDTO>>().withData(response);
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
