package vn.viettel.common.service;

import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface ApParamService extends BaseService {
    ApParamDTO getApParamById(Long id);
    List<ApParamDTO> getCardTypes();
    ApParamDTO getReason(Long id);
    List<ApParamDTO> getCloselytypes();
    List<ApParamDTO> getReasonNotImport();
    List<ApParamDTO> getByType(String type);
    List<ApParamDTO> findAll();
    ApParamDTO getByCode(String code);
    List<ApParamDTO> getSalesChannel();

//    /*
//    lấy danh sách theo list id
//     */
//    List<ApParamDTO> getApParamByIds(List<Long> apParamIds);
}
