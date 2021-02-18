package vn.viettel.core.dto.user;

import vn.viettel.core.db.entity.City;

import java.util.List;

public class CityDTO {
    List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
