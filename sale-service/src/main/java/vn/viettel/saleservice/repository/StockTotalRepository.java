package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.SOConfirm;
import vn.viettel.core.db.entity.StockTotal;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockTotalRepository extends BaseRepository<StockTotal> {
    @Query(value = "SELECT st.* FROM stock_total st " +
            //"join warehouse wh on wh.id = st.warehouse_id " +
            //"join warehouse wh on wh.id = st.warehouse_id " +
            "join receiptimport reci reci.warehouse_id = st.warehouse_id " +
            "join po_confirm pc pc.po_no = reci.po_number " +
            "join so_confirm sc sc.po_confirm_id = pc.id " +
            " where 1 = 1 and st.product_id = :product_id ", nativeQuery = true)
    StockTotal findStockTotalByProductId(Long product_id);
}
