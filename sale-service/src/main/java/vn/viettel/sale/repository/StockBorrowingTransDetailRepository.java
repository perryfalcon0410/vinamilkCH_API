package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockBorrowingTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.Date;
import java.util.List;

public interface StockBorrowingTransDetailRepository extends BaseRepository<StockBorrowingTransDetail> {

    List<StockBorrowingTransDetail> getStockBorrowingTransDetailByTransId(Long id);

    @Query(value = "SELECT dtl FROM StockBorrowingTransDetail dtl WHERE dtl.transId =:transId ")
    List<StockBorrowingTransDetail> getStockBorrowingTransDetail(Long transId);
    
}
