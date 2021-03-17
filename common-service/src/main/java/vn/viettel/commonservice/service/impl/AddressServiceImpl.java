package vn.viettel.commonservice.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.commonservice.repository.*;
import vn.viettel.commonservice.service.AddressService;
import vn.viettel.commonservice.service.dto.*;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;

import java.util.ArrayList;
import java.util.List;

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
    public Response<List<LocationResponse>> getAllCountry() {
        List<LocationResponse> listCountry = new ArrayList<>();
        Response<List<LocationResponse>> response = new Response<>();

        for (Country c : countryRepo.findAll())
            listCountry.add(setLocation(c, null, null, null, null));

        response.setData(listCountry);
        return response;
    }

    @Override
    public Response<List<LocationResponse>> getAllProvince() {
        List<LocationResponse> listProvince = new ArrayList<>();
        Response<List<LocationResponse>> response = new Response<>();

        for (Province pro : proRepo.findAll())
            listProvince.add(setLocation(pro.getCountry(), pro, null, null, null));

        response.setData(listProvince);
        return response;
    }

    @Override
    public Response<List<LocationResponse>> getAllDistrict() {
        List<LocationResponse> listDistrict = new ArrayList<>();
        Response<List<LocationResponse>> response = new Response<>();

        for (District dis : disRepo.findAll())
            listDistrict.add(setLocation(dis.getProvince().getCountry(), dis.getProvince(), dis, null, null));
        response.setData(listDistrict);
        return response;
    }

    @Override
    public Response<List<LocationResponse>> getAllWard() {
        List<LocationResponse> listWard = new ArrayList<>();
        Response<List<LocationResponse>> response = new Response<>();

        for (Ward ward : wardRepo.findAll())
            listWard.add(setLocation(ward.getDistrict().getProvince().getCountry(),
                    ward.getDistrict().getProvince(), ward.getDistrict(), ward, null));

        response.setData(listWard);
        return response;
    }

    @Override
    public Response<List<LocationResponse>> getAllAddress() {
        List<LocationResponse> listAddress = new ArrayList<>();
        Response<List<LocationResponse>> response = new Response<>();

        for (Address adr : addRepo.findAll())
            listAddress.add(setLocation(adr.getWard().getDistrict().getProvince().getCountry(),
                    adr.getWard().getDistrict().getProvince(),
                    adr.getWard().getDistrict(), adr.getWard(), adr));

        response.setData(listAddress);
        return response;
    }

    @Override
    public Response<List<LocationResponse>> getDistrictByProvinceId(long id) {
        List<LocationResponse> listDistrict = new ArrayList<>();
        Response<List<LocationResponse>> response = new Response<>();

        List<District> districts = disRepo.getDistrictByProvinceId(id);
        for (District dis : districts) {
            listDistrict.add(setLocation(dis.getProvince().getCountry(), dis.getProvince(),
                    dis,null, null));
        }
        response.setData(listDistrict);
        return response;
    }

    @Override
    public Response<List<LocationResponse>> getWardByDistrict(long id) {
        List<LocationResponse> listWard = new ArrayList<>();
        Response<List<LocationResponse>> response = new Response<>();

        List<Ward> wards = wardRepo.getByDistrictId(id);

        for(Ward ward: wards) {
            listWard.add(setLocation(ward.getDistrict().getProvince().getCountry(),
                    ward.getDistrict().getProvince(), ward.getDistrict(), ward, null));
        }
        response.setData(listWard);
        return response;
    }

    public LocationResponse setLocation(Country country, Province province,
                                        District district, Ward ward, Address address) {
        LocationResponse response = new LocationResponse();
        if (country != null) {
            response.setCountry(new CountryDto(country.getId(), country.getName()));
        }
        if (province != null) {
            response.setProvince(new ProvinceDto(province.getId(), province.getName()));
        }
        if (district != null) {
            response.setDistrict(new DistrictDto(district.getId(), district.getName()));
        }
        if (ward != null) {
            response.setWard(new WardDto(ward.getId(), ward.getName()));
        }
        if (address != null) {
            response.setAddress(new AddressDto(address.getId(), address.getName()));
        }
        return response;
    }

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

    @Override
    public CreateAddressResponse createAddress(CreateAddressDto createAddressDto) {
        Address address = new Address(createAddressDto.getAddress());
        CreateAddressResponse response = new CreateAddressResponse();
        try {
            Ward ward = wardRepo.findById(createAddressDto.getWardId()).get();
            address.setWard(ward);
            addRepo.save(address);

            response.setAddressId(address.getId());
            response.setWardId(ward.getId());
        } catch (Exception e) {
            return null;
        }
        return response;
    }

}
