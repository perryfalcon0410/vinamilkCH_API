package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.MediaItem;

import java.util.Optional;

public interface MediaItemRepository extends BaseRepository<MediaItem>, JpaSpecificationExecutor<MediaItem> {

    @Query(value = "SELECT * FROM MEDIA_ITEM " +
            "WHERE OBJECT_ID =:productId AND OBJECT_TYPE = 3 AND STATUS = 1 ", nativeQuery = true )
    Optional<MediaItem> getImageProduct(Long productId);


}
