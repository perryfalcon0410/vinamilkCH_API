package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.MediaItem;

public interface MediaItemRepository extends BaseRepository<MediaItem>, JpaSpecificationExecutor<MediaItem> {

}
