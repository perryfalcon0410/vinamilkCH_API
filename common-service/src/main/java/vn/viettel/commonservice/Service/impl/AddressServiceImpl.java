package vn.viettel.commonservice.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.commonservice.Repository.*;
import vn.viettel.commonservice.Service.AddressService;
import vn.viettel.core.db.entity.*;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    CountryRepository countryRepo;
    @Autowired
    ProvinceRepository proRepo;
    @Autowired
    DistrictRepository disRepo;
    @Autowired
    WardRepository wardRepo;
    @Autowired
    AddressRepository addRepo;


    @Override
    public String getCountryById(long id) {
        Country country = countryRepo.findById(id).get();
        if(country != null)
            return country.getName();
        return null;
    }

    @Override
    public String getProvinceById(long id) {
        Province province = proRepo.findById(id).get();
        if(province != null)
            return province.getName();
        return null;
    }

    @Override
    public String getDistrictById(long id) {
        District district = disRepo.findById(id).get();
        if(district != null)
            return district.getName();
        return null;
    }

    @Override
    public String getWardById(long id) {
        Ward ward = wardRepo.findById(id).get();
        if(ward != null)
            return ward.getName();
        return null;
    }

    @Override
    public String getAddressById(long id) {
        Address address = addRepo.findById(id).get();
        if(address != null)
            return address.getName();
        return null;
    }

}
