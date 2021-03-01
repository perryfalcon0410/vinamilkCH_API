package vn.viettel.core.dto.salon;

public interface SalonStylesDBResponseDTO {

    Long getId();

    Long getSalonId();

    String getName();

    String getDescription();

    String getPhotoURL();

    Double getCost();

    String getTag();

    Long getSalonStyleTypeId();

    String getSalonStyleTypeName();

    Long getSalonStyleGroupId();

    String getSalonStyleGroupName();

    Double getStyleCost();
}
