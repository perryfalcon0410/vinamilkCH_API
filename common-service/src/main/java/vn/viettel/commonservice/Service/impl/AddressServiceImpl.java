package vn.viettel.commonservice.Service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.commonservice.Repository.*;
import vn.viettel.commonservice.Service.AddressService;
import vn.viettel.commonservice.Service.dto.ProDisDto;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;

import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    ModelMapper modelMapper;

    @Override
    public String getCountryById(long id) {
        try {
            Country country = countryRepo.findById(id).get();
            return country.getName();
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Override
    public String getProvinceById(long id) {
        try {
            Province province = proRepo.findById(id).get();
            return province.getName();
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Override
    public String getDistrictById(long id) {
        try {
            District district = disRepo.findById(id).get();
            return district.getName();
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Override
    public String getWardById(long id) {
        try {
            Ward ward = wardRepo.findById(id).get();
            return ward.getName();
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Override
    public String getAddressById(long id) {
        try {
            Address address = addRepo.findById(id).get();
            return address.getName();
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Override
    public Response<List<ProDisDto>> getDistrictByProId(long id) {
        Response<List<ProDisDto>> response = new Response<>();

        try {
            List<ProDisDto> locations = disRepo.getDistrictByProId(id);
            response.setData(locations);
            return response;
        } catch (Exception e) {
            System.out.println(e);
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
            return response;
        }
    }

    @Override
    public Response<List<Ward>> getWardByDistrictId(long id) {
        Response<List<Ward>> response = new Response<>();

        try {
            List<Ward> wards = wardRepo.getByDistrictId(id);
            response.setData(wards);
            return response;
        } catch (Exception e) {
            System.out.println(e);
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
            return response;
        }
    }

}
