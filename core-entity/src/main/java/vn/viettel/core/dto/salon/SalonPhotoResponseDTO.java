package vn.viettel.core.dto.salon;

import java.time.LocalDateTime;
import java.util.List;

public class SalonPhotoResponseDTO {
    private Long salonId;
    private String photoUrl;
    private List<String> photoUrls;

    public SalonPhotoResponseDTO() {
    }

    public SalonPhotoResponseDTO(Long salonId, String photoUrl, List<String> photoUrls) {
        this.salonId = salonId;
        this.photoUrl = photoUrl;
        this.photoUrls = photoUrls;
    }

    public Long getSalonId() {
        return salonId;
    }

    public void setSalonId(Long salonId) {
        this.salonId = salonId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }
}
