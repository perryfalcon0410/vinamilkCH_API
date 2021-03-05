package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.SOConfirm;
import vn.viettel.core.db.entity.StockTotal;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockTotalRepository extends BaseRepository<StockTotal> {
    @Query(value = "SELECT distinct st.* FROM stock_total st " +
            "join receiptimport reci on reci.warehouse_id = st.warehouse_id " +
            "join po_confirm pc on pc.po_no = reci.po_number " +
            "join so_confirm sc on sc.po_confirm_id = pc.id " +
            " where 1 = 1 and st.product_id = :product_id ", nativeQuery = true)
    StockTotal findStockTotalConfirmByProductId(Long product_id);

    @Query(value = "SELECT distinct st.* FROM stock_total st " +
            "join receiptimport reci on reci.warehouse_id = st.warehouse_id " +
            "join po_adjusted pa on pa.po_license_number = reci.po_number " +
            "join po_adjusted_detail pad on pad.po_adjusted_id = pa.id " +
            " where 1 = 1 and st.product_id = :product_id ", nativeQuery = true)
    StockTotal findStockTotalAdjustedByProductId(Long product_id);

    @Query(value = "SELECT distinct st.* FROM stock_total st " +
            "join receiptimport reci on reci.warehouse_id = st.warehouse_id " +
            "join po_borrow pb on pb.po_borrow_number = reci.po_number " +
            "join po_borrow_detail pbd on pbd.po_borrow_id = pb.id " +
            " where 1 = 1 and st.product_id = :product_id ", nativeQuery = true)
    StockTotal findStockTotalBorrowByProductId(Long product_id);
}
