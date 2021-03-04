package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POAdjustedDetail;
import vn.viettel.core.db.entity.SOConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface SOConfirmRepository extends BaseRepository<SOConfirm> {
    @Query(value = "SELECT so.* FROM so_confirm so " +
            "join po_confirm po on po.id = so.po_confirm_id " +
            " where 1 = 1 and is_free_item = 1 and so.po_confirm_id = paId", nativeQuery = true)
    List<SOConfirm> getListProductPromotional(Long paId);

    @Query(value = "SELECT so.* FROM so_confirm so " +
            "join po_confirm po on po.id = so.po_confirm_id " +
            " where 1 = 1 and is_free_item = 0 and so.po_confirm_id = paId", nativeQuery = true)
    List<SOConfirm> getListProduct(Long paId);
}
