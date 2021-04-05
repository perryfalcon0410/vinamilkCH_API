package vn.viettel.sale.service;

import org.springframework.web.bind.annotation.GetMapping;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface ApParamService extends BaseService {
    Response<ApParam> getApParamById(Long id);
    Response<List<ApParam>> getCardTypes();

    Response<List<ApParam>> getCloselytypes();

}
