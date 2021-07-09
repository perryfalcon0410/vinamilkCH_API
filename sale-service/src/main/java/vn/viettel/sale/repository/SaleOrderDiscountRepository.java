package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrderDiscount;

import java.util.List;

public interface SaleOrderDiscountRepository extends BaseRepository<SaleOrderDiscount> {

    @Query("Select count(distinct s.id) From SaleOrder s Join SaleOrderDiscount so On s.id = so.saleOrderId " +
            " Where s.shopId =:shopId And s.customerId =:customerId And s.type =:type And so.promotionProgramId = :promotionProgramId " +
            " And year(s.orderDate) = year(sysdate) And month(s.orderDate) = month(sysdate) And day(s.orderDate) = day(sysdate)" )
    Integer countDiscount(Long shopId, Long customerId, Long promotionProgramId, Integer type);

    @Query("Select new vn.viettel.core.dto.promotion.PromotionProgramDTO(so.promotionProgramId, COUNT ( DISTINCT CASE WHEN s.type = 1 THEN s.id END  ) - count( DISTINCT CASE WHEN s.type = 2 THEN s.id END  ) )" +
            " From SaleOrder s Join SaleOrderDiscount so On s.id = so.saleOrderId " +
            " Where s.shopId =:shopId And s.customerId =:customerId And so.promotionProgramId IN (:promotionIds) " +
            " And year(s.orderDate) = year(sysdate) And month(s.orderDate) = month(sysdate) And day(s.orderDate) = day(sysdate) " +
            " GROUP BY so.promotionProgramId " )
    List<PromotionProgramDTO> countDiscountUsed(Long shopId, Long customerId, List<Long> promotionIds);

    List<SaleOrderDiscount> findAllBySaleOrderId(Long saleOrderId);
}
