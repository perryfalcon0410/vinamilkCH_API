package vn.viettel.sale.service.impl;

import org.joda.time.DateTime;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.dto.OrderProductOnlineDTO;
import vn.viettel.sale.service.feign.*;
import vn.viettel.sale.specification.OnlineOrderSpecification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OnlineOrderServiceImpl extends BaseServiceImpl<OnlineOrder, OnlineOrderRepository> implements OnlineOrderService {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    OnlineOrderDetailRepository onlineOrderDetailRepo;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    AreaClient areaClient;

    @Autowired
    ApparamClient apparamClient;

    @Autowired
    CustomerTypeClient customerTypeClient;

    @Autowired
    MemberCustomerClient memberCustomerClient;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

    @Autowired
    StockTotalRepository stockTotalRepo;

    @Override
    public Page<OnlineOrderDTO> getOnlineOrders(OnlineOrderFilter filter, Pageable pageable) {
        Page<OnlineOrder> onlineOrders = repository.findAll(
                Specification.where(
                        OnlineOrderSpecification.hasOrderNumber(filter.getOrderNumber()))
                        .and(OnlineOrderSpecification.hasShopId(filter.getShopId()))
                        .and(OnlineOrderSpecification.hasSynStatus(filter.getSynStatus()))
                        .and(OnlineOrderSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate())
                        ), pageable);
        Page<OnlineOrderDTO> onlineOrderDTOS = onlineOrders.map(this::mapOnlineOrderToOnlineOrderDTO);

        return onlineOrderDTOS;
    }

    @Override
    public OnlineOrderDTO getOnlineOrder(Long id, Long shopId, Long userId) {
        OnlineOrder onlineOrder = repository.findById(id)
                .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
        if(onlineOrder.getSynStatus()==1)
            throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);

        CustomerDTO customerDTO = customerClient.getCustomerByMobiPhoneV1(onlineOrder.getCustomerPhone()).getData();

        if(customerDTO == null) {
            CustomerRequest customerRequest = this.createCustomerRequest(onlineOrder);
            try{
                customerDTO = customerClient.createForFeignV1(customerRequest, userId,  shopId).getData();
            }catch (Exception e){

                throw new ValidateException(ResponseMessage.CUSTOMER_CREATE_FAILED);
            }
        }else{
            RptCusMemAmountDTO rptCusMemAmountDTO = memberCustomerClient.findByCustomerIdV1(customerDTO.getId()).getData();
            if(rptCusMemAmountDTO != null && rptCusMemAmountDTO.getScore()!=null) {
                customerDTO.setScoreCumulated(rptCusMemAmountDTO.getScore());
                customerDTO.setAmountCumulated(rptCusMemAmountDTO.getScore()*100D);
            }

        }

        List<OnlineOrderDetail> orderDetails = onlineOrderDetailRepo.findByOnlineOrderId(id);
        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);

        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        List<OrderProductOnlineDTO> products = new ArrayList<>();
        for (OnlineOrderDetail detail: orderDetails) {
            OrderProductOnlineDTO productOrder = this.mapOnlineOrderDetailToProductDTO(
                detail, onlineOrderDTO, customerDTO.getCustomerTypeId(), shopId, customerTypeDTO.getWareHouseTypeId());
            products.add(productOrder);
        }
        onlineOrderDTO.setProducts(products);
        onlineOrderDTO.setCustomer(customerDTO);

        return onlineOrderDTO;
    }

    @Override
    public String checkOnlineNumber(String code) {
        ApParamDTO apParam = apparamClient.getApParamByCodeV1("NUMDAY_CHECK_ONLNO").getData();
        if(apParam == null) throw new ValidateException(ResponseMessage.AP_PARAM_NOT_EXISTS);
        Date date = new Date();
        Date daysAgo = new DateTime(date).minusDays(Integer.valueOf(apParam.getValue())).toDate();
        List<OnlineOrder> onlineOrders = repository.findAll(Specification.where(OnlineOrderSpecification.equalOrderNumber(code))
            .and(OnlineOrderSpecification.hasFromDateToDate(daysAgo, date)));
        if(!onlineOrders.isEmpty())
            throw new ValidateException(ResponseMessage.ONLINE_NUMBER_IS_EXISTS);
        return code;
    }

    private CustomerRequest createCustomerRequest(OnlineOrder onlineOrder) {
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCustomerTypeDefaultV1().getData();

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setFirstName(this.getFirstName(onlineOrder.getCustomerName()));
        customerRequest.setLastName(this.getLastName(onlineOrder.getCustomerName()));
        customerRequest.setMobiPhone(onlineOrder.getCustomerPhone());
        customerRequest.setDob(onlineOrder.getCustomerDOB());
        customerRequest.setCustomerTypeId(customerTypeDTO.getId());
        customerRequest.setStatus(1L);
        this.setArea(onlineOrder.getCustomerAddress(), customerRequest);
        return customerRequest;
    }

    private void setArea(String address, CustomerRequest customerRequest ) {
            String[] words = address.split(",");
            int index = words.length -1;
            String provinceName = words[index--].trim();
            String districtName = words[index--].trim();
            String precinctName = words[index--].trim();
            String street = words[index].trim();

            AreaDTO areaDTO = areaClient.getAreaV1(provinceName, districtName, precinctName).getData();
            customerRequest.setAreaId(areaDTO.getId());
            customerRequest.setStreet(street);
    }

    private OrderProductOnlineDTO mapOnlineOrderDetailToProductDTO(
            OnlineOrderDetail detail, OnlineOrderDTO onlineOrderDTO, Long customerTypeId, Long shopId, Long warehouseTypeId) {

        Product product = productRepo.getProductByProductCodeAndStatus(detail.getSku(), 1)
                .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));

        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if(productPrice == null)
            throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductOnlineDTO productOrder = modelMapper.map(product, OrderProductOnlineDTO.class);
        productOrder.setProductId(product.getId());
        productOrder.setQuantity(detail.getQuantity());
        productOrder.setPrice(productPrice.getPrice());
        productOrder.setPrice(productPrice.getPrice());
        productOrder.setStockTotal(stockTotal.getQuantity());

        onlineOrderDTO.addQuantity(productOrder.getQuantity());
        onlineOrderDTO.addTotalPrice(productOrder.getTotalPrice());

        return productOrder;
    }


    private OnlineOrderDTO mapOnlineOrderToOnlineOrderDTO(OnlineOrder order) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OnlineOrderDTO dto = modelMapper.map(order, OnlineOrderDTO.class);
        dto.setOrderInfo(order.getCustomerName() + " - " + order.getCustomerPhone());
        return dto;
    }

    private String getLastName(String fullName) {
        int i = fullName.lastIndexOf(' ');
        return fullName.substring(0, i).trim();
    }

    private String getFirstName(String fullName) {
        int i = fullName.lastIndexOf(' ');
        return fullName.substring(i+1).trim();
    }


}
