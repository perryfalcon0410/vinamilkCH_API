package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.ComboProductDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ComboProductDetailRepository extends BaseRepository<ComboProductDetail> {
    List<ComboProductDetail> findByComboProductIdAndStatus(Long comboId, Integer status);

    @Query("select cbDtl from ComboProductDetail cbDtl join ComboProduct cb on cb.id = cbDtl.comboProductId " +
            " where cb.id in :comboIds and (:status is null or cb.status = :status ) ")
    List<ComboProductDetail> getComboProductDetail(List<Long> comboIds, Integer status);
}
