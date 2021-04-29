package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.feign.MemberCustomerClient;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.specification.OnlineOrderSpecification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OnlineOrderServiceImpl extends BaseServiceImpl<OnlineOrder, OnlineOrderRepository> implements OnlineOrderService {

    @Autowired
    OnlineOrderDetailRepository onlineOrderDetailRepo;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CustomerClient customerClient;

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
    public Response<Page<OnlineOrderDTO>> getOnlineOrders(OnlineOrderFilter filter, Pageable pageable) {
        if (filter.getFromDate() == null || filter.getToDate() == null) {
            LocalDate initial = LocalDate.now();
            filter.setFromDate(Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            filter.setToDate(new Date());
        }
        Page<OnlineOrder> onlineOrders = repository.findAll(
                Specification.where(
                        OnlineOrderSpecification.hasOrderNumber(filter.getOrderNumber()))
                        .and(OnlineOrderSpecification.hasShopId(filter.getShopId()))
                        .and(OnlineOrderSpecification.hasSynStatus(filter.getSynStatus()))
                        .and(OnlineOrderSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate())
                        .and(OnlineOrderSpecification.hasDeletedAtIsNull())), pageable);
        Page<OnlineOrderDTO> onlineOrderDTOS = onlineOrders.map(this::mapOnlineOrderToOnlineOrderDTO);

        return new Response<Page<OnlineOrderDTO>>().withData(onlineOrderDTOS);
    }

    @Override
    public Response<OnlineOrderDTO> getOnlineOrder(Long id, Long shopId, Long userId) {
        OnlineOrder onlineOrder = repository.findById(id)
                .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
        if(onlineOrder.getSynStatus()==1)
            throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);

        CustomerDTO customerDTO = customerClient.getCustomerByMobiPhoneV1(onlineOrder.getCustomerPhone()).getData();

        if(customerDTO == null) {
            CustomerRequest customerRequest = this.createCustomerRequest(onlineOrder, shopId);
            try{
                customerDTO = customerClient.createForFeignV1(customerRequest, shopId, userId).getData();
            }catch (Exception e){
                throw new ValidateException(ResponseMessage.CUSTOMER_CREATE_FAILED);
            }
        }else{
            RptCusMemAmountDTO rptCusMemAmountDTO = memberCustomerClient.findByCustomerIdV1(customerDTO.getId()).getData();
            if(rptCusMemAmountDTO != null) {
                customerDTO.setScoreCumulated(rptCusMemAmountDTO.getScore());
                customerDTO.setAmountCumulated(rptCusMemAmountDTO.getAmount());
            }

        }

        List<OnlineOrderDetail> orderDetails = onlineOrderDetailRepo.findByOnlineOrderId(id);
        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);

        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCusTypeIdByShopIdV1(shopId);
        if(customerTypeDTO == null)
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);

        List<OrderProductDTO> products = new ArrayList<>();
        for (OnlineOrderDetail detail: orderDetails) {
            OrderProductDTO productOrder = this.mapOnlineOrderDetailToProductDTO(
                detail, onlineOrderDTO, customerDTO.getCustomerTypeId(), shopId, customerTypeDTO.getWareHoseTypeId());
            products.add(productOrder);
        }
        onlineOrderDTO.setProducts(products);
        onlineOrderDTO.setCustomer(customerDTO);

        return new Response<OnlineOrderDTO>().withData(onlineOrderDTO);
    }

    private CustomerRequest createCustomerRequest(OnlineOrder onlineOrder, Long shopId) {
        ShopDTO shop = shopClient.getByIdV1(shopId).getData();
        CustomerTypeDTO customerTypeDTO = customerTypeClient.getCustomerTypeDefaultV1().getData();

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setFirstName(this.getFirstName(onlineOrder.getCustomerName()));
        customerRequest.setLastName(this.getLastName(onlineOrder.getCustomerName()));
        customerRequest.setAddress(onlineOrder.getCustomerAddress());
        customerRequest.setMobiPhone(onlineOrder.getCustomerPhone());
        customerRequest.setDob(onlineOrder.getCustomerDOB());
        customerRequest.setGenderId(3);
        customerRequest.setAreaId(shop.getAreaId());
        customerRequest.setCustomerTypeId(customerTypeDTO.getId());
        customerRequest.setStatus(1L);
        customerRequest.setStreet("");

        return customerRequest;
    }

    private OrderProductDTO mapOnlineOrderDetailToProductDTO(
            OnlineOrderDetail detail, OnlineOrderDTO onlineOrderDTO, Long customerTypeId, Long shopId, Long warehouseTypeId) {

        Product product = productRepo.getProductByProductCodeAndStatus(detail.getSku(), 1)
                .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));

        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if(productPrice == null)
            throw new ValidateException(ResponseMessage.NO_PRICE_APPLIED);

        StockTotal stockTotal = stockTotalRepo.getStockTotal(shopId, warehouseTypeId, product.getId())
                .orElseThrow(() -> new ValidateException(ResponseMessage.STOCK_TOTAL_NOT_FOUND));

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderProductDTO productOrder = modelMapper.map(product, OrderProductDTO.class);
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
