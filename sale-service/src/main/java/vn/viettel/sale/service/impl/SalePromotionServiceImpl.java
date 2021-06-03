package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.enums.PromotionCustObjectType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.PromotionProductRequest;
import vn.viettel.sale.repository.SaleOrderDiscountRepository;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.dto.AutoPromotionDTO;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.MemberCardClient;
import vn.viettel.sale.service.feign.PromotionClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SalePromotionServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SalePromotionService {

    @Autowired
    PromotionClient promotionClient;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    MemberCardClient memberCardClient;

    @Autowired
    SaleOrderDiscountRepository saleOrderDiscountRepo;

    @Override
    public List<AutoPromotionDTO> getFreeItems(PromotionProductRequest request, Long shopId, Long customerId) {
        // Danh sách chương trình khuyến mãi thỏa các điều kiện cửa hàng, khách hàng
        List<PromotionProgramDTO> programs = this.validPromotionProgram(request, shopId, customerId);

        List<AutoPromotionDTO> autoPromotionDTOS = new ArrayList<>();
        // Tính khuyến mãi từ zv01 -> zv21




        return null;
    }


    //Kiểm tra các chwuong trình hợp lệ
    public List<PromotionProgramDTO> validPromotionProgram(PromotionProductRequest request, Long shopId, Long customerId) {

        List<PromotionProgramDTO> programs = promotionClient.findPromotionPrograms(shopId).getData();
        // Kiểm tra loại đơn hàng tham gia & Kiểm tra thuộc tính khách hàng tham gia
        List<PromotionProgramDTO> programDTOS = programs.stream().filter(program -> {
            //Kiểm tra giới hạn số lần được KM của KH
            Integer numberDiscount = saleOrderDiscountRepo.countDiscount(shopId, customerId);
            if(program.getPromotionDateTime() != null && program.getPromotionDateTime() <= numberDiscount) return false;

            // Kiểm tra loại đơn hàng tham gia
            if(program.getObjectType() != null || program.getObjectType() != 0) {
                Set<Long> orderTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.ORDER_TYPE.getValue()).getData();
                if(!orderTypes.contains(Long.valueOf(request.getOrderType()))) return false;
            }
            // Kiểm tra thuộc tính khách hàng tham gia
            CustomerDTO customer = customerClient.getCustomerByIdV1(customerId).getData();
            if(customer == null) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
            Set<Long> customerTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_TYPE.getValue()).getData();
            if(!customerTypes.isEmpty() && !customerTypes.contains(customer.getCustomerTypeId())) return false;

            Set<Long> memberCards = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.MEMBER_CARD.getValue()).getData();
            MemberCardDTO memberCard = memberCardClient.getByCustomerId(customer.getId()).getData();
            if(!memberCards.isEmpty() && !memberCards.contains(memberCard!=null?memberCard.getId():null)) return false;

            Set<Long> layalCustomers = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.LOYAL_CUSTOMER.getValue()).getData();
            if(!layalCustomers.isEmpty() && !layalCustomers.contains(customer.getCloselyTypeId())) return false;

            Set<Long> customerCardTypes = promotionClient.findCusCardPromotion(program.getId(), PromotionCustObjectType.CUSTOMER_CARD_TYPE.getValue()).getData();
            if(!customerCardTypes.isEmpty() && !customerCardTypes.contains(customer.getCardTypeId())) return false;

            return true;
        }).collect(Collectors.toList());

        return programDTOS;
    }


}








