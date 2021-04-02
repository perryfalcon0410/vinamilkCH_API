package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.AreaRepository;
import vn.viettel.customer.service.AreaService;

import java.util.List;

@Service
public class AreaServiceImpl extends BaseServiceImpl<Area, AreaRepository> implements AreaService {
    @Override
    public Response<List<Area>> getAllByType(Integer type) {
        List<Area> areas = repository.getAllByType(type);
        return new Response<List<Area>>().withData(areas);
    }
}
