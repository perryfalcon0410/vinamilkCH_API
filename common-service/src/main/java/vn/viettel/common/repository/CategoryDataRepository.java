package vn.viettel.common.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.CategoryData;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface CategoryDataRepository extends BaseRepository<CategoryData> {
    @Query(value = "SELECT * FROM CATEGORY_DATA WHERE CATEGORY_GROUP_CODE = 'MASTER_CHANGE'", nativeQuery = true)
    List<CategoryData> findByCategoryGroupCode();

    @Query(value = "SELECT * FROM CATEGORY_DATA WHERE CATEGORY_GROUP_CODE = 'MASTER_CHANGE' AND ID = :id", nativeQuery = true)
    CategoryData getReasonById(Long id);

    @Query(value = "SELECT * FROM CATEGORY_DATA WHERE CATEGORY_GROUP_CODE = 'MASTER_CHANGE'", nativeQuery = true)
    List<CategoryData> listReasonExchangeTrans();
}
