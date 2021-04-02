package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.CustomerTypeService;

import java.util.List;

@Service
public class CustomerTypeServiceImpl extends BaseServiceImpl<CustomerType, CustomerTypeRepository> implements CustomerTypeService {

    @Override
    public Response<List<CustomerType>> getAll() {
        List<CustomerType> customerTypes = repository.findAll();
        return new Response<List<CustomerType>>().withData(customerTypes);
    }
}
