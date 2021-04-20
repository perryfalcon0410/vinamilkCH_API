package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface SaleOrderDetailRepository extends BaseRepository<SaleOrderDetail> {
    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL", nativeQuery = true)
    List<SaleOrderDetail> getListSaleOrderDetail();

    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL WHERE IS_FREE_ITEM = 0 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<SaleOrderDetail> getBySaleOrderId(Long ID);

    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL WHERE IS_FREE_ITEM = 1 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<SaleOrderDetail> getSaleOrderDetailPromotion(Long ID);

    SaleOrderDetail findByProductIdAndSaleOrderId(Long productId, Long saleOrderId);
}
