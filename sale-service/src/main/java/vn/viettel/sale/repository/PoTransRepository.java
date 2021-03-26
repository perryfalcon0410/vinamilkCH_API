package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface PoTransRepository extends BaseRepository<PoTrans>, JpaSpecificationExecutor<PoTrans> {
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS", nativeQuery = true)
    int getQuantityPoTrans();
    Optional<PoTrans> getPoTransByIdAndDeletedAtIsNull(Long tranId);

}
