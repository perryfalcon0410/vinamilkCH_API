package vn.viettel.core.dto.user;

import org.springframework.beans.factory.annotation.Value;

public interface FilledPlacesResponseDTO {
    Long getPlaceId();

    Long getCityId();

    Long getPrefectureId();

    String getPostalCode();

    String getPlaceName();

    String getCityName();

    String getPrefectureName();

}
