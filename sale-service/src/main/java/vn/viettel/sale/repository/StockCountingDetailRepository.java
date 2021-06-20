package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;
import vn.viettel.sale.service.dto.StockCountingExcel;

import java.util.List;

public interface StockCountingDetailRepository extends BaseRepository<StockCountingDetail>, JpaSpecificationExecutor<StockCountingDetail> {
    List<StockCountingDetail> findByStockCountingId(Long id);
//    @Query(value = "SELECT * FROM STOCK_COUNTING_DETAIL sd JOIN PRODUCTS p ON sd.PRODUCT_ID = p.ID WHERE p.STATUS = 1 " +
//            "AND STOCK_COUNTING_ID = :id ORDER BY sd.PRODUCT_ID asc", nativeQuery = true)
//    Page<StockCountingDetail> findByStockCountingId(Long id, Pageable pageable);

    @Query(value = "SELECT new vn.viettel.sale.service.dto.StockCountingExcel(p.id, p.productCode, p.productName, gcat.productInfoName, " +
            " cat.productInfoName, dtl.price, dtl.stockQuantity, p.uom1, p.uom2, coalesce(p.convFact, 1), dtl.quantity) " +
            " FROM StockCountingDetail dtl JOIN Product p ON p.id = dtl.productId " +
            " LEFT JOIN ProductInfo gcat ON p.groupCatId = gcat.id and gcat.type = 6 " +
            " LEFT JOIN ProductInfo cat ON p.catId = gcat.id and gcat.type = 1 " +
            " WHERE dtl.stockCountingId = :stockCountingId " +
            " ORDER BY p.productCode asc")
    Page<StockCountingExcel> getStockCountingExcel(Long stockCountingId, Pageable pageable);
}
