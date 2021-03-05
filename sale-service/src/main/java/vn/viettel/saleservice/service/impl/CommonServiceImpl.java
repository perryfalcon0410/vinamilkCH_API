package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.Reason;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.ReasonRepository;
import vn.viettel.saleservice.service.CommonService;
import vn.viettel.saleservice.service.dto.ReasonDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    ReasonRepository reasonRepository;

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
}
