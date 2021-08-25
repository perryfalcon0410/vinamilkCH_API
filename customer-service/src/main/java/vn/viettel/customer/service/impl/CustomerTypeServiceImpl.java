package vn.viettel.customer.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.CustomerTypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerTypeServiceImpl extends BaseServiceImpl<CustomerType, CustomerTypeRepository> implements CustomerTypeService {

    @Override
    public List<CustomerTypeDTO> getAll(Boolean isCreate) {
        List<CustomerType> customerTypes = repository.findAll();
        if(isCreate){
            return customerTypes.stream()
                    .filter(customerType -> customerType.getStatus() == 1 && customerType.getPosModifyCustomer() == 1)
                    .map(customerType -> modelMapper.map(customerType, CustomerTypeDTO.class))
                    .collect(Collectors.toList());
        }else {
            return customerTypes.stream()
                    .filter(customerType -> customerType.getStatus() == 1)
                    .map(customerType -> modelMapper.map(customerType, CustomerTypeDTO.class))
                    .collect(Collectors.toList());
        }

    }

    @Override
    public List<CustomerTypeDTO> findByIds(List<Long> customerTypeIds) {
        List<CustomerType> findByIds = repository.findByIds(customerTypeIds);
        if (findByIds == null) return null;
        return findByIds.stream().map(item -> modelMapper.map(item, CustomerTypeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Long getWarehouseTypeByShopId(Long shopId) {
        List<CustomerTypeDTO> customerTypes = repository.getWareHouseTypeIdByShopId(shopId);
        if(customerTypes == null || customerTypes.isEmpty()) customerTypes = repository.getCustomerTypeDefault();
        if(customerTypes!=null || !customerTypes.isEmpty()) return customerTypes.get(0).getWareHouseTypeId();
        return null;
    }

    @Override
    public CustomerTypeDTO getCusTypeByShopId(long shopId) {
        List<CustomerTypeDTO> customerTypes = repository.getWareHouseTypeIdByShopId(shopId);
        if(customerTypes == null || customerTypes.isEmpty()) return null;
        return customerTypes.get(0);
    }

    @Override
    public CustomerTypeDTO getCustomerTypeDefaut() {
        List<CustomerTypeDTO> customerTypes = repository.getCustomerTypeDefault();
        if(customerTypes == null || customerTypes.isEmpty())
            throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
        return customerTypes.get(0);
    }

    @Override
    public List<CustomerTypeDTO> findByWarehouse(Long warehouseId) {
        List<CustomerType> customerTypes = repository.getByWarehouse(warehouseId);
        if(customerTypes == null || customerTypes.isEmpty()) return new ArrayList<>();
        return customerTypes.stream().map(item -> modelMapper.map(item, CustomerTypeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public CustomerTypeDTO getCustomerType(Long customerId, Long shopId) {
        List<CustomerTypeDTO> customerTypes = repository.getByCustomerId(customerId);
        if(customerTypes.isEmpty()) customerTypes = repository.getWareHouseTypeIdByShopId(shopId);
        if(!customerTypes.isEmpty()) return customerTypes.get(0);
        return null;
    }
}
