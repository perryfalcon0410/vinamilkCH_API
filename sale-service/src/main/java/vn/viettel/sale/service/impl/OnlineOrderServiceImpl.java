package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.db.entity.sale.OnlineOrderDetail;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.CustomerRequest;
import vn.viettel.sale.messaging.OnlineOrderFilter;
import vn.viettel.sale.repository.OnlineOrderDetailRepository;
import vn.viettel.sale.repository.OnlineOrderRepository;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.CustomerDTO;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.specification.OnlineOrderSpecification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OnlineOrderServiceImpl extends BaseServiceImpl<OnlineOrder, OnlineOrderRepository> implements OnlineOrderService {

    @Autowired
    OnlineOrderDetailRepository onlineOrderDetailRepo;

//    @Autowired
//    ShopRepository shopRepository;

    @Override
    public Response<Page<OnlineOrderDTO>> getOnlineOrders(
            OnlineOrderFilter filter, Pageable pageable) {

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<OnlineOrderDTO> getOnlineOrder(Long id, Long shopid) {
        OnlineOrder onlineOrder = repository.getOnlineOrderByIdAndShopId(id, shopid)
            .orElseThrow(() -> new ValidateException(ResponseMessage.ORDER_ONLINE_NOT_FOUND));
        if(onlineOrder.getSynStatus()==1)
            throw new ValidateException(ResponseMessage.SALE_ORDER_ALREADY_CREATED);

        List<ProductDTO> productDTOS = onlineOrderDetailRepo.findByOnlineOrderId(id)
            .stream().map(this::mapOnlineOrderDetailToProductDTO).collect(Collectors.toList());

        CustomerRequest customerRequest = this.createCustomerRequest(onlineOrder, shopid);
        // save customer;


        CustomerDTO customerDTO = new CustomerDTO();
        OnlineOrderDTO onlineOrderDTO = this.mapOnlineOrderToOnlineOrderDTO(onlineOrder);
        onlineOrderDTO.setProducts(productDTOS);
        onlineOrderDTO.setCustomer(customerDTO);

        return null;
    }

    private CustomerRequest createCustomerRequest(OnlineOrder onlineOrder, Long shopId) {
//        Shop shop = shopRepository.findById(shopId).orElseThrow(
//            () -> new ValidateException(ResponseMessage.SHOP_NOT_FOUND));
//        String fullName = onlineOrder.getCustomerName().trim();
//        int i = fullName.lastIndexOf(' ');
//        String lastName = fullName.substring(i+1);
//        String firstName = fullName.substring(0, i-1);
//        CustomerRequest customerRequest = new CustomerRequest();
//            customerRequest.setFirstName(firstName);
//            customerRequest.setLastName(lastName);
//            customerRequest.setAddress(onlineOrder.getCustomerAddress());
//            customerRequest.setPhone(onlineOrder.getCustomerPhone());
//            customerRequest.setDob(onlineOrder.getCustomerDOB());
//            customerRequest.setAreaId(shop.getAreaId());
//            customerRequest.setGenderId(3);
//          //  customerRequest.setCustomerTypeId();
//
//        return customerRequest;
        return null;
    }

    private ProductDTO mapOnlineOrderDetailToProductDTO(OnlineOrderDetail detail) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductDTO productDTO = new ProductDTO();

        return productDTO;
    }


    private OnlineOrderDTO mapOnlineOrderToOnlineOrderDTO(OnlineOrder order) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OnlineOrderDTO dto = modelMapper.map(order, OnlineOrderDTO.class);
        dto.setOrderInfo(order.getCustomerName() + " - " + order.getCustomerPhone());
        return dto;
    }
}
