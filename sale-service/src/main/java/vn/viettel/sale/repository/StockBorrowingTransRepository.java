package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.sale.entities.StockBorrowingTrans;
import vn.viettel.core.repository.BaseRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockBorrowingTransRepository extends BaseRepository<StockBorrowingTrans>, JpaSpecificationExecutor<StockBorrowingTrans> {

    @Lock(LockModeType.PESSIMISTIC_WRITE )
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    @Query(value = "SELECT p FROM StockBorrowingTrans p " +
            " WHERE p.createdAt>= :startDate And p.type =:type AND p.transCode like :startWith% " +
            " ORDER BY p.transCode desc ")
    Page<StockBorrowingTrans> getLastTransCode(Integer type, String startWith, LocalDateTime startDate, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE )
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    @Query(value = "SELECT p FROM StockBorrowingTrans p " +
            " WHERE p.createdAt>= :startDate And p.type =:type AND p.redInvoiceNo like :startWith% " +
            " ORDER BY p.redInvoiceNo desc ")
    Page<StockBorrowingTrans> getLastRedInvoiceNo(Integer type, String startWith, LocalDateTime startDate, Pageable pageable);

}
