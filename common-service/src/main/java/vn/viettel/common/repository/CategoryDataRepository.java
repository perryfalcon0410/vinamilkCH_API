package vn.viettel.common.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.CategoryData;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface CategoryDataRepository extends BaseRepository<CategoryData> {

    @Query(value = "SELECT cat FROM CategoryData cat WHERE cat.categoryGroupCode = 'MASTER_CHANGE'")
    List<CategoryData> findByCategoryGroupCode();

    @Query(value = "SELECT cat FROM CategoryData cat WHERE cat.categoryGroupCode = 'MASTER_CHANGE' AND cat.id = :id")
    CategoryData getReasonById(Long id);

    @Query(value = "SELECT cat FROM CategoryData cat WHERE cat.categoryGroupCode = 'MASTER_CHANGE' AND cat.status = 1")
    List<CategoryData> listReasonExchangeTrans();
}
