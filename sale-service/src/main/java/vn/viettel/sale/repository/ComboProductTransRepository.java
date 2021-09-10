package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ComboProductTrans;
import vn.viettel.sale.service.dto.TotalDTO;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;

public interface ComboProductTransRepository extends BaseRepository<ComboProductTrans>, JpaSpecificationExecutor<ComboProductTrans> {

    @Lock(LockModeType.PESSIMISTIC_WRITE )
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    @Query(value = "SELECT ct FROM ComboProductTrans ct WHERE ct.transCode LIKE :startWith% AND ct.shopId =:shopId ORDER BY ct.transCode DESC ")
    Page<ComboProductTrans> getLastOrderNumber(Long shopId, String startWith, Pageable pageable);

    @Query(value = "SELECT new vn.viettel.sale.service.dto.TotalDTO( sum(ex.totalQuantity), sum (ex.totalAmount)) " +
            " FROM ComboProductTrans ex WHERE ( :transType is null or ex.transType = :transType ) " +
            " AND ( :transCode is null or upper(ex.transCode) LIKE %:transCode%) " +
            " AND  ex.shopId = :shopId " +
            " AND (ex.transDate BETWEEN :fromDate AND :toDate ) " +
            "")
    TotalDTO getExchangeTotal(Long shopId, String transCode, Integer transType, LocalDateTime fromDate, LocalDateTime toDate);
}
