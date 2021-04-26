package vn.viettel.common.service;

import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface ApParamService extends BaseService {
    Response<ApParamDTO> getApParamById(Long id);
    Response<List<ApParamDTO>> getCardTypes();
    Response<List<ApParamDTO>> getReason(Long id);

    Response<List<ApParamDTO>> getCloselytypes();

}
