package vn.viettel.commonservice.Service;

public interface AddressService {
    String getCountryById(long id);
    String getProvinceById(long id);
    String getDistrictById(long id);
    String getWardById(long id);
    String getAddressById(long id);
}
