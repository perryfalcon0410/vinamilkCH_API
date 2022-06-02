package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.sale.messaging.OrderPromotionRequest;

@Service
public class DefinedObject {
    public OrderPromotionRequest newOrderPromotionRequest(){
        return new OrderPromotionRequest();
    }
}
