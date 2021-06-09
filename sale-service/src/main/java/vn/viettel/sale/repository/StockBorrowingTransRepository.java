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
    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS WHERE TYPE =1 AND STATUS =1", nativeQuery = true)
    int getQuantityStockBorrowingTrans();

    @Query(value = "SELECT COUNT(ID) FROM STOCK_BORROWING_TRANS WHERE TYPE = 2 AND STATUS =1 ", nativeQuery = true)
    int getQuantityStockBorrowingTransExport();

    StockBorrowingTrans getStockBorrowingTransById(Long transId);

    Optional<StockBorrowingTrans> getByTransCodeAndStatus(String transCode, Integer status);
}
