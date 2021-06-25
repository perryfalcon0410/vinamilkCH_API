package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrderDiscount;

import java.util.List;

public interface SaleOrderDiscountRepository extends BaseRepository<SaleOrderDiscount> {

    @Query("Select count(distinct s.id) From SaleOrder s Join SaleOrderDiscount so On s.id = so.saleOrderId " +
            " Where s.shopId =:shopId And s.customerId =:customerId And s.type =:type And so.promotionProgramId = :promotionProgramId " +
            " And year(s.orderDate) = year(sysdate) And month(s.orderDate) = month(sysdate) And day(s.orderDate) = day(sysdate)" )
    Integer countDiscount(Long shopId, Long customerId, Long promotionProgramId, Integer type);


//    @Query(value = "SELECT * " +
//            "FROM sale_order_discount WHERE sale_order_id = :id ", nativeQuery = true)
    List<SaleOrderDiscount> findBySaleOrderIdAndProductId(Long id, Long productId);

    List<SaleOrderDiscount> findAllBySaleOrderId(Long saleOrderId);
}
