package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.CustomerTypeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerTypeServiceImpl extends BaseServiceImpl<CustomerType, CustomerTypeRepository> implements CustomerTypeService {

    @Override
    public Response<List<CustomerTypeDTO>> getAll() {
        List<CustomerType> customerTypes = repository.findAll();
        List<CustomerTypeDTO> customerTypeDTOS = customerTypes.stream()
                .filter(customerType -> customerType.getStatus() == 1)
                .map(customerType -> modelMapper.map(customerType, CustomerTypeDTO.class))
                .collect(Collectors.toList());

        return new Response<List<CustomerTypeDTO>>().withData(customerTypeDTOS);
    }

    @Override
    public Response<CustomerTypeDTO> findById(Long id) {
        Optional<CustomerType> customerType = repository.findById(id);
        if (!customerType.isPresent()) {
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        }
        return new Response<CustomerTypeDTO>().withData(modelMapper.map(customerType.get(), CustomerTypeDTO.class));

    }

    @Override
    public CustomerTypeDTO getCusTypeByShopId(long shopId) {
        CustomerType customerType = repository.getWareHouseTypeIdByShopId(shopId);

        return modelMapper.map(customerType, CustomerTypeDTO.class);
    }

    @Override
    public Response<CustomerTypeDTO> getCustomerTypeDefaut() {
        CustomerType customerType = repository.getCustomerTypeDefault()
                .orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS));
        return new Response<CustomerTypeDTO>().withData(modelMapper.map(customerType, CustomerTypeDTO.class));
    }

    @Override
    public Response<CustomerTypeDTO> findByCustomerTypeId(Long customerTypeId) {
        CustomerType customerType = repository.findCustomerTypeById(customerTypeId).orElse(null);
        if (customerType != null) {
            return new Response<CustomerTypeDTO>().withData(modelMapper.map(customerType, CustomerTypeDTO.class));
        } else {
            return null;
        }
    }

}
