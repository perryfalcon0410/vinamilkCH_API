package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface SaleOrderDetailRepository extends BaseRepository<SaleOrderDetail> {
    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL", nativeQuery = true)
    List<SaleOrderDetail> getListSaleOrderDetail();

    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL WHERE IS_FREE_ITEM = 0 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<SaleOrderDetail> getBySaleOrderId(Long ID);

    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL WHERE IS_FREE_ITEM = 1 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<SaleOrderDetail> getSaleOrderDetailPromotion(Long ID);

    SaleOrderDetail findByProductIdAndSaleOrderId(Long productId, Long saleOrderId);

    List<SaleOrderDetail> findAllBySaleOrderId(Long id);
    List<SaleOrderDetail> getSaleOrderDetailBySaleOrderId(Long id);

    @Query(value = "SELECT SUM(quantity) as totalQuantity from sale_order_detail WHERE sale_order_id = ?1  and product_id = ?2", nativeQuery = true)
    Float getCountQuantity(Long saleOrderId, Long productId);

    @Query(value = "SELECT product_id FROM sale_order_detail WHERE sale_order_id = ?1" , nativeQuery = true)
    List<BigDecimal> findAllBySaleOrderCode(Long saleOrderId);

    SaleOrderDetail findSaleOrderDetailBySaleOrderIdAndProductId(Long saleOrderId, Long ids);
}
