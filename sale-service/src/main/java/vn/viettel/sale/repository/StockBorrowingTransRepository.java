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
    @Query(value = "SELECT COUNT(sbt.id) FROM StockBorrowingTrans sbt WHERE sbt.type = 1 AND sbt.status = 1")
    int getQuantityStockBorrowingTrans();

    @Query(value = "SELECT COUNT(sbt.id) FROM StockBorrowingTrans sbt WHERE sbt.type = 2 AND sbt.status = 1")
    int getQuantityStockBorrowingTransExport();

    StockBorrowingTrans getStockBorrowingTransById(Long transId);

}
