package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.ApParamRepository;
import vn.viettel.sale.service.ApParamService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApParamServiceImpl extends BaseServiceImpl<ApParam, ApParamRepository> implements ApParamService {

    @Override
    public Response<ApParam> getApParamById(Long id) {
        return new Response<ApParam>().withData(repository.findById(id).get());
    }

    @Override
    public Response<List<ApParam>> getCardTypes() {
        List<ApParam> cardTypes = repository.findAll().stream()
                .filter(ap->ap.getType().equals("SALEMT_CUSTOMER_CARD")).collect(Collectors.toList());
        return new Response<List<ApParam>>().withData(cardTypes);
    }

    @Override
    public Response<List<ApParam>> getCloselytypes() {
        List<ApParam> cardTypes = repository.findAll().stream()
                .filter(ap->ap.getType().equals("SALEMT_CLOSELY_CUSTOMER")).collect(Collectors.toList());
        return new Response<List<ApParam>>().withData(cardTypes);
    }


}