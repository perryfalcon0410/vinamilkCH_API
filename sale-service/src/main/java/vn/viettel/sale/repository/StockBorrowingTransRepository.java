package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowingTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface StockBorrowingTransRepository extends BaseRepository<StockBorrowingTrans>, JpaSpecificationExecutor<StockBorrowingTrans> {
    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS", nativeQuery = true)
    int getQuantityStockBorrowingTrans();

    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS WHERE STATUS = 2 ", nativeQuery = true)
    int getQuantityStockBorrowingTransExport();

    StockBorrowingTrans getStockBorrowingTransById(Long transId);

    @Query(value = "SELECT * FROM STOCK_BORROWING_TRANS WHERE TYPE = 1 ", nativeQuery = true)
    Page<StockBorrowingTrans> getStockBorrowingTransImport(Specification<StockBorrowingTrans> and, Pageable pageable);

    @Query(value = "SELECT * FROM STOCK_BORROWING_TRANS WHERE TYPE = 2 ", nativeQuery = true)
    Page<StockBorrowingTrans> getStockBorrowingTransExport(Specification<StockBorrowingTrans> and, Pageable pageable);

    Optional<StockBorrowingTrans> getByTransCodeAndStatus(String transCode, Integer status);
}
