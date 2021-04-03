package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.AreaRepository;
import vn.viettel.customer.service.AreaService;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Response<List<Area>> getProvinces() {
        List<Area> areas = this.getAll().getData();
        return new Response<List<Area>>().withData(areas.stream().filter(a->a.getType() == 1).collect(Collectors.toList()));
    }

    @Override
    public Response<List<Area>> getDistrictsByProvinceId(Long provinceId) {
        List<Area> districts = this.getAll().getData().stream()
                .filter(a->a.getType() == 2 && a.getParentAreaId() == provinceId).collect(Collectors.toList());
        return new Response<List<Area>>().withData(districts);
    }

    @Override
    public Response<List<Area>> getPrecinctsByDistrictId(Long districtId) {
        List<Area> precincts = this.getAll().getData().stream()
                .filter(a->a.getType() == 3 && a.getParentAreaId() == districtId).collect(Collectors.toList());
        return new Response<List<Area>>().withData(precincts);
    }
}
