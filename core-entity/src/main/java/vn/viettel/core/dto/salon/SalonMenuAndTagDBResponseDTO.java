package vn.viettel.core.dto.salon;

public interface SalonMenuAndTagDBResponseDTO {
    Integer getSalonId();
    Integer getSalonMenuId();
    String getMenuName();
    String getMenuTag();
    String getDescription();
    Double getCost();
    Integer getTime();
    Short getOptional();
    Double getTax();
    Boolean getDisplay();
}
