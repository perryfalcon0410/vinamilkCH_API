package vn.viettel.customer.service.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.entities.CustomerType_;
import vn.viettel.customer.entities.Customer_;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.CustomerTypeService;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerTypeServiceImpl extends BaseServiceImpl<CustomerType, CustomerTypeRepository> implements CustomerTypeService {

    @Override
    public List<CustomerTypeDTO> getAll() {
        List<CustomerType> customerTypes = repository.findAll();

        return customerTypes.stream()
                .filter(customerType -> {
                    if(customerType.getStatus() == 1 && customerType.getPosModifyCustomer() == 1)
                        return true;
                    else
                        return false;
                })
                .map(customerType -> modelMapper.map(customerType, CustomerTypeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerTypeDTO> findByIds(List<Long> customerTypeIds) {
        List<CustomerType> findByIds = repository.findByIds(customerTypeIds);
        if (findByIds == null) return null;
        return findByIds.stream().map(item -> modelMapper.map(item, CustomerTypeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public CustomerTypeDTO getCusTypeByShopId(long shopId) {
        CustomerType customerType = repository.getWareHouseTypeIdByShopId(shopId);
        return modelMapper.map(customerType, CustomerTypeDTO.class);
    }

    @Override
    public CustomerTypeDTO getCustomerTypeDefaut() {
        CustomerType customerType = repository.getCustomerTypeDefault()
                .orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS));
        return modelMapper.map(customerType, CustomerTypeDTO.class);
    }

    @Override
    public CustomerTypeDTO findByCustomerTypeId(Long customerTypeId) {
        CustomerType customerType = repository.findCustomerTypeById(customerTypeId).orElse(null);
        if (customerType != null) {
            return modelMapper.map(customerType, CustomerTypeDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public Long getWarehouseTypeIdByCustomer(Long id) {
        return repository.findWarehouseTypeIdByCustomer(id);
    }
}
