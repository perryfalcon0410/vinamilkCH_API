package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.PromotionProductRequest;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.service.SalePromotionService;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;
import vn.viettel.sale.service.feign.PromotionClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalePromotionServiceImpl extends BaseServiceImpl<SaleOrder, SaleOrderRepository> implements SalePromotionService {

    @Autowired
    PromotionClient promotionClient;

    @Override
    public List<ZmFreeItemDTO> getFreeItems(PromotionProductRequest request, Long shopId, Long customerId) {
        // Danh sách chương trình khuyến mãi hiện tại shop được hưởng
        List<PromotionProgramDTO> programs = promotionClient.findPromotionPrograms(shopId).getData();
        if(programs.isEmpty()) return null;

        // Kiểm tra loại đơn hàng tham gia
        List<PromotionProgramDTO> programDTOS = programs.stream().filter(program -> {
            if(program.getObjectType() != null || program.getObjectType() != 0) {






                return false;
            }
            return true;
        }).collect(Collectors.toList());


        // Kiểm tra thuộc tính khách hàng tham gia





        //Kiểm tra giới hạn số làn được KM của KH





        return null;
    }
}
