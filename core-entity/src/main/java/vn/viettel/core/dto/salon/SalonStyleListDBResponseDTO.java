package vn.viettel.core.dto.salon;

public interface SalonStyleListDBResponseDTO {

    Long getSalonStyleId();
    Long getSalonId();
    Long getSalonStyleTypeId();
    String getName();
    String getDescription();
    String getPhotoURL();
    Double getCost();
    Double getTax();
    Boolean getDisplay();

}
