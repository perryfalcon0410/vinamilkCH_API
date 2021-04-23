package vn.viettel.sale.repository;

import vn.viettel.sale.entities.ComboProductDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ComboProductDetailRepository extends BaseRepository<ComboProductDetail> {
    List<ComboProductDetail> findByComboProductIdAndStatus(Long comboId, Integer status);
}
