package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.db.entity.common.Price;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.db.entity.sale.OnlineOrderDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.CustomerRequest;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.OnlineOrderDetailRepository;
import vn.viettel.sale.repository.OnlineOrderRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.CustomerDTO;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.dto.OnlineOrderProductDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
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
    ProductRepository productRepo;

    @Autowired
    ProductPriceRepository productPriceRepo;

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
                        .and(OnlineOrderSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate())), pageable);
        Page<OnlineOrderDTO> onlineOrderDTOS = onlineOrders.map(this::mapOnlineOrderToOnlineOrderDTO);

        return new Response<Page<OnlineOrderDTO>>().withData(onlineOrderDTOS);
    }


    public Response<OnlineOrderDTO> getOnlineOrder(Long id, Long shopId) {
        OnlineOrder onlineOrder = repository.getOnlineOrderByIdAndShopId(id, shopId)
                .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
        if(onlineOrder.getSynStatus()==1)
            throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);

        CustomerDTO customerDTO = customerClient.getCustomerByPhone(onlineOrder.getCustomerPhone(),
                this.getLastName(onlineOrder.getCustomerName()), this.getFirstName(onlineOrder.getCustomerName()), shopId).getData();

        if(customerDTO.getId() == null) {
            CustomerRequest customerRequest = this.createCustomerRequest(onlineOrder, shopId);
            try{
                customerDTO = customerClient.createForFeign(customerRequest, shopId).getData();
            }catch (Exception e){
                throw new ValidateException(ResponseMessage.CUSTOMER_CREATE_FALE);
            }
        }

        List<OnlineOrderDetail> orderDetails = onlineOrderDetailRepo.findByOnlineOrderId(id);
        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);
        List<OnlineOrderProductDTO> products = new ArrayList<>();
        for (OnlineOrderDetail detail: orderDetails) {
            OnlineOrderProductDTO productOrder = this.mapOnlineOrderDetailToProductDTO(detail, onlineOrderDTO, customerDTO.getCustomerTypeId());
            products.add(productOrder);
        }
        onlineOrderDTO.setProducts(products);
        onlineOrderDTO.setCustomer(customerDTO);

        return new Response<OnlineOrderDTO>().withData(onlineOrderDTO);
    }

    private CustomerRequest createCustomerRequest(OnlineOrder onlineOrder, Long shopId) {
        Shop shop = shopClient.getById(shopId).getData();
        CustomerType customerType = customerTypeClient.getCustomerTypeDefault().getData();

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setFirstName(this.getFirstName(onlineOrder.getCustomerName()));
        customerRequest.setLastName(this.getLastName(onlineOrder.getCustomerName()));
        customerRequest.setAddress(onlineOrder.getCustomerAddress());
        customerRequest.setPhone(onlineOrder.getCustomerPhone());
        customerRequest.setDob(onlineOrder.getCustomerDOB());
        customerRequest.setGenderId(3);
        customerRequest.setShopId(shop.getId());
        customerRequest.setAreaId(shop.getAreaId());
        customerRequest.setCustomerTypeId(customerType.getId());
        customerRequest.setStatus(1L);

        return customerRequest;
    }

    private OnlineOrderProductDTO mapOnlineOrderDetailToProductDTO(OnlineOrderDetail detail, OnlineOrderDTO onlineOrderDTO,  Long customerTypeId) {

        Product product = productRepo.getProductByProductCodeAndDeletedAtIsNull(detail.getSku())
                .orElseThrow(() -> new ValidateException(ResponseMessage.PRODUCT_NOT_FOUND));
        Price productPrice = productPriceRepo.getProductPrice(product.getId(), customerTypeId);
        if(productPrice == null)
            throw new ValidateException(ResponseMessage.PRODUCT_PRICE_NOT_FOUND);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OnlineOrderProductDTO productOrder = modelMapper.map(product, OnlineOrderProductDTO.class);
        productOrder.setQuantity(detail.getQuantity());
        productOrder.setPrice(productPrice.getPrice());

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
