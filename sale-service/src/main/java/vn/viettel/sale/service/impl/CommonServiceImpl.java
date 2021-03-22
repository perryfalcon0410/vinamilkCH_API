package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.Reason;
import vn.viettel.core.db.entity.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ReasonRepository;
import vn.viettel.saleservice.repository.ShopRepository;
import vn.viettel.saleservice.service.CommonService;
import vn.viettel.saleservice.service.dto.ReasonDTO;
import vn.viettel.saleservice.service.dto.ShopDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    ReasonRepository reasonRepository;
    @Autowired
    ShopRepository shopRepository;

    @Override
    public Response<List<ReasonDTO>> getAllReason() {
        List<Reason> reasons = reasonRepository.findAll();
        List<ReasonDTO> reasonList = new ArrayList<>();
        for(Reason re : reasons)
        {
            ReasonDTO reason = new ReasonDTO();
            reason.setReasonName(re.getReasonName());
            reasonList.add(reason);
        }
        Response<List<ReasonDTO>> response = new Response<>();
        response.setData(reasonList);
        return response;
    }

    @Override
    public Response<ShopDTO> getShopById(long shopId) {
        Response<ShopDTO> response = new Response<>();
        Shop s = shopRepository.findById(shopId).get();
        ShopDTO shopDTO = new ShopDTO();
        if (s != null) {

            shopDTO.setId(s.getId());
            shopDTO.setShopCode(s.getShopCode());
            shopDTO.setShopName(s.getShopName());
            shopDTO.setStatus(s.getStatus());
            response.setData(shopDTO);

        }
        return response;
    }
}
