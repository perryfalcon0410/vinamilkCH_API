package vn.viettel.core.dto.salon;

public interface SalonMenuDBResponseDTO {
    Long getId();

    Long getSalonId();

    String getName();

    Long getType();

    String getTypeName();

    Integer getTime();

    String getDescription();

    Double getCost();

    Short getOptional();

    String getTagName();
}
