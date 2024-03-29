package vn.viettel.sale.repository;

import java.time.LocalDateTime;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.StockBorrowingTrans;

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

    @Query(value = "SELECT p FROM StockBorrowingTrans p WHERE p.id=:id And p.shopId =:shopId And p.type =:type AND p.status =1")
    StockBorrowingTrans getByIdAndShopId(Long id, Long shopId, Integer type);

}
