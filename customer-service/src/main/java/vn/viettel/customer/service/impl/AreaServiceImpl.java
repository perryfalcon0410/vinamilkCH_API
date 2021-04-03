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
    public Response<List<Area>> getAll() {
        List<Area> areas = repository.findAll();
        return new Response<List<Area>>().withData(areas);
    }

    @Override
    public Response<Area> getAreaById(Long id) {
        return new Response<Area>().withData(repository.findByIdAndDeletedAtIsNull(id));
    }
}
