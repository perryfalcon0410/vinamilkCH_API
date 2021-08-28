package vn.viettel.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.CategoryData;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface CategoryDataRepository extends BaseRepository<CategoryData> {

    @Query(value = "SELECT cat FROM CategoryData cat WHERE cat.categoryGroupCode = 'MASTER_CHANGE'")
    List<CategoryData> findByCategoryGroupCode();

    @Query(value = "SELECT cat FROM CategoryData cat WHERE cat.categoryGroupCode = 'MASTER_CHANGE' AND cat.id = :id")
    CategoryData getReasonById(Long id);

    @Query(value = "SELECT new vn.viettel.core.dto.common.CategoryDataDTO(cat.id, cat.categoryCode, cat.categoryName, cat.categoryGroupCode, cat.remarks, cat.parentCode) " +
            "FROM CategoryData cat WHERE cat.categoryGroupCode = 'MASTER_CHANGE' AND cat.status = 1")
    List<CategoryDataDTO> listReasonExchangeTrans();

    @Query(value = "SELECT new vn.viettel.core.dto.common.CategoryDataDTO(cat.id, cat.categoryCode, cat.categoryName, cat.categoryGroupCode, cat.remarks, cat.parentCode) " +
            "FROM CategoryData cat WHERE cat.id IN (:ids) AND cat.categoryGroupCode = 'MASTER_CHANGE' AND cat.status = 1")
    Page<CategoryDataDTO> listReasonExchangeTransId(List<Long> ids, Pageable pageable);

}
