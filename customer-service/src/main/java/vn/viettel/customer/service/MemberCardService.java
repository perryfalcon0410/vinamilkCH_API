package vn.viettel.customer.service;

import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.entities.MemberCard;


import java.util.List;

@Service
public interface MemberCardService extends BaseService {
    MemberCardDTO getMemberCardById(Long id);
    MemberCard create(MemberCardDTO memberCardDTO,Long userId);
    MemberCard update(MemberCardDTO memberCardDTO);
    List<MemberCardDTO> getMemberCardByCustomerId(Long id);
}
