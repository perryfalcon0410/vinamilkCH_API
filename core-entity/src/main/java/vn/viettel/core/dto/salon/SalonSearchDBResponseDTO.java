package vn.viettel.core.dto.salon;

import java.util.List;

public interface SalonSearchDBResponseDTO {
    Long getSalonId();

    String getSalonName();

    String getAreaName();

    String getAddress();

    String getRegularHoliday();

    Short getParkingLot();

    String getNearestStation();

    String getCommitmentCondition();

    String getSalonBusinessHours();

    String getSlug();

    String getCommitmentConditionName();

    List<String> getListCommitmentConditionName();
}
