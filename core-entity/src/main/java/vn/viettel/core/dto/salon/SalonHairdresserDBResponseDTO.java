package vn.viettel.core.dto.salon;

import org.springframework.beans.factory.annotation.Value;

public interface SalonHairdresserDBResponseDTO {
    Long getId();

    String getName();

    @Value("#{target.privilege_name}")
    String getPrivilegeName();

    String getDescription();

    @Value("#{new Double({target.beautician_cost})}")
    Double getBeauticianCost();

    @Value("#{target.photo_url}")
    String getPhotoUrl();
}
