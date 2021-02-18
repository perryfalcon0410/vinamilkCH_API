package vn.viettel.core.dto.user;

import vn.viettel.core.db.entity.Place;

import java.util.List;

public class PlaceDTO {
    List<Place> Places;

    public List<Place> getPlaces() {
        return Places;
    }

    public void setPlaces(List<Place> places) {
        Places = places;
    }
}
