package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.UserClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderReturnImpl implements OrderReturnService {

    @Autowired
    SaleOrderRepository saleOrderRepository;
    @Autowired
    UserClient userClient;
    @Autowired
    CustomerClient customerClient;

    public Response<Page<OrderReturnDTO>> getAllOrderReturn(Pageable pageable) {
        Response<Page<OrderReturnDTO>> response = new Response<>();
        List<OrderReturnDTO> orderReturnDTOList = new ArrayList<>();
        List<SaleOrder> orderReturnList = saleOrderRepository.getListOrderReturn();
        for (SaleOrder orderReturn:orderReturnList) {
            SaleOrder saleOrder = saleOrderRepository.findById(orderReturn.getFromSaleOrderId()).get();
            User user = userClient.getUserById(orderReturn.getSalemanId());
            Customer customer = customerClient.getCustomerById(orderReturn.getCustomerId()).getData();

            OrderReturnDTO orderReturnDTO = new OrderReturnDTO();
            orderReturnDTO.setId(orderReturn.getId());
            orderReturnDTO.setOrderReturnNumber(orderReturn.getOrderNumber());
            orderReturnDTO.setOrderNumber(saleOrder.getOrderNumber());
            orderReturnDTO.setUserName(user.getFirstName()+" "+user.getLastName());
            orderReturnDTO.setCustomerNumber(customer.getCustomerCode());
            orderReturnDTO.setCustomerName(customer.getFirstName()+" "+customer.getLastName());
            orderReturnDTO.setDateReturn(orderReturn.getCreatedAt());
            orderReturnDTO.setAmount(orderReturn.getAmount());
            orderReturnDTO.setDiscount(orderReturn.getAutoPromotion() + orderReturn.getZmPromotion());
            orderReturnDTO.setPromotion(orderReturn.getTotalPromotion());
            orderReturnDTO.setTotal(orderReturn.getTotal());
            orderReturnDTO.setCreatedAt(orderReturn.getCreatedAt());
            orderReturnDTOList.add(orderReturnDTO);
        }
        Page<OrderReturnDTO> orderReturnDTOResponse = new PageImpl<>(orderReturnDTOList);
        response.setData(orderReturnDTOResponse);
        return response;
    }
}
