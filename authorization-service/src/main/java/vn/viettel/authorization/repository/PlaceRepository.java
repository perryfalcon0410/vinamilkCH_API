package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Place;
import vn.viettel.core.dto.user.FilledPlacesResponseDTO;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends BaseRepository<Place> {

    List<Place> findAllByCityId(long cityId);
    List<Place> findAllByPostalCode(String postalCode);

    @Query(value = "SELECT "
            + "     pl.place_id as placeId, "
            + "     c.city_id as cityId, "
            + "     p.prefecture_id as prefectureId, "
            + "     pl.postal_code as postalCode,"
            + "     pl.name as placeName,"
            + "     c.name as cityName,"
            + "     p.name as prefectureName "
            + "FROM places AS pl "
            + "JOIN cities AS c ON pl.city_id = c.city_id  "
            + "JOIN prefectures AS p ON p.prefecture_id = c.prefecture_id   "
            + "WHERE pl.postal_code = :postalCode ", nativeQuery = true)
    List<FilledPlacesResponseDTO> getAllByPostalCode(@Param("postalCode") String postalCode);

    @Query(value = "SELECT "
            + "     pl.place_id as placeId, "
            + "     c.city_id as cityId, "
            + "     p.prefecture_id as prefectureId, "
            + "     pl.postal_code as postalCode,"
            + "     pl.name as placeName,"
            + "     c.name as cityName,"
            + "     p.name as prefectureName "
            + "FROM places AS pl "
            + "JOIN cities AS c ON pl.city_id = c.city_id  "
            + "JOIN prefectures AS p ON p.prefecture_id = c.prefecture_id   "
            + "WHERE pl.place_id = :placeId ", nativeQuery = true)
    FilledPlacesResponseDTO getByPlaceId(@Param("placeId") Long placeId);
}
