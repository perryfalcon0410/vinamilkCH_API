package vn.viettel.core.dto.salon;

public class SalonConfirmationBookingHairPhotoDetailDTO {
    private Long id;

    private String photoUrl;

    public SalonConfirmationBookingHairPhotoDetailDTO() {
    }

    public SalonConfirmationBookingHairPhotoDetailDTO(Long id, String photoUrl) {
        this.id = id;
        this.photoUrl = photoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
