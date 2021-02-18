package vn.viettel.core.dto.user;

import vn.viettel.core.db.entity.Region;

import java.util.List;

public class RegionDTO {
    List<Region> Regions;

    public List<Region> getRegions() {
        return Regions;
    }

    public void setRegions(List<Region> regions) {
        Regions = regions;
    }
}
