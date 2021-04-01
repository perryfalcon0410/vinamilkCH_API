package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.ApParamRepository;
import vn.viettel.sale.service.ApParamService;

import java.util.Optional;

@Service
public class ApParamServiceImpl extends BaseServiceImpl<ApParam, ApParamRepository> implements ApParamService {

    @Override
    public Optional<ApParam> getApParamById(Long id) {
        return repository.findById(id);
    }
}
