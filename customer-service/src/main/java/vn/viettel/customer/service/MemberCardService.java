package vn.viettel.customer.service;

import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.entities.MemberCard;


import java.util.List;

@Service
public interface MemberCardService extends BaseService {
    Response<MemberCardDTO> getMemberCardById(Long id);
    Response<MemberCard> create(MemberCardDTO memberCardDTO,Long userId);
    Response<MemberCard> update(MemberCardDTO memberCardDTO);
    Response<List<MemberCardDTO>> getMemberCardByCustomerId(Long id);
}
