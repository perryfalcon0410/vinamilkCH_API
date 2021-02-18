package vn.viettel.core.dto.salon;

public interface SalonStyleTagDBResponseDTO {

    Long getSalonStyleId();
    Long getSalonId();
    Long getSalonStyleTypeId();
    String getName();
    String getDescription();
    String getPhotoURL();
    String getTag();
    Double getTax();
    Double getCost();
    Boolean getDisplay();
}
