package vn.viettel.core.dto.salon;

import java.util.List;

public class SalonMenuNameResponseDTO {
    private String salonMenuId;
    private List<Long> salonMenuIds;
    private String salonMenuName;

    public SalonMenuNameResponseDTO() {
    }

    public SalonMenuNameResponseDTO(String salonMenuId, List<Long> salonMenuIds, String salonMenuName) {
        this.salonMenuId = salonMenuId;
        this.salonMenuIds = salonMenuIds;
        this.salonMenuName = salonMenuName;
    }

    public String getSalonMenuId() { return salonMenuId; }

    public void setSalonMenuId(String salonMenuId) {
        this.salonMenuId = salonMenuId;
    }

    public List<Long> getSalonMenuIds() {
        return salonMenuIds;
    }

    public void setSalonMenuIds(List<Long> salonMenuIds) {
        this.salonMenuIds = salonMenuIds;
    }

    public String getSalonMenuName() {
        return salonMenuName;
    }

    public void setSalonMenuName(String salonMenuName) {
        this.salonMenuName = salonMenuName;
    }
}
