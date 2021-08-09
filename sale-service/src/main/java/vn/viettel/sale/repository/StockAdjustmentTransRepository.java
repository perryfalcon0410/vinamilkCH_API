package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import vn.viettel.sale.entities.StockAdjustmentTrans;
import vn.viettel.core.repository.BaseRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;

public interface StockAdjustmentTransRepository extends BaseRepository<StockAdjustmentTrans>, JpaSpecificationExecutor<StockAdjustmentTrans> {

    @Lock(LockModeType.PESSIMISTIC_WRITE )
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    @Query(value = "SELECT p FROM StockAdjustmentTrans p " +
            " WHERE p.createdAt>= :startDate And p.type =:type AND p.internalNumber like :codeStart% " +
            " ORDER BY p.internalNumber desc ")
    Page<StockAdjustmentTrans> getLastInternalCode(Integer type, String codeStart, LocalDateTime startDate, Pageable pageable);
}