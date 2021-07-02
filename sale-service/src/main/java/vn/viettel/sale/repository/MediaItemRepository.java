package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.MediaItem;

import java.util.Optional;

public interface MediaItemRepository extends BaseRepository<MediaItem>, JpaSpecificationExecutor<MediaItem> {

}
