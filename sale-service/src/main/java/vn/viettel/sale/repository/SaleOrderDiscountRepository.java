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

    @Query("Select new vn.viettel.core.dto.promotion.PromotionProgramDTO(so.promotionProgramId, COUNT( DISTINCT s.id ))" +
            " From SaleOrder s Join SaleOrderDiscount so On s.id = so.saleOrderId " +
            " Where s.shopId =:shopId And s.customerId =:customerId And so.promotionProgramId IN (:promotionIds) And s.type = 1 " +
            " And year(s.orderDate) = year(sysdate) And month(s.orderDate) = month(sysdate) And day(s.orderDate) = day(sysdate) " +
            " And s.id NOT IN ( " +
                " Select DISTINCT rt.fromSaleOrderId From SaleOrder rt Join SaleOrderDiscount rtd On rt.id = rtd.saleOrderId " +
                " Where rt.shopId =:shopId And rt.customerId =:customerId And rtd.promotionProgramId IN (:promotionIds) And rt.type = 2 " +
                " And year(rt.orderDate) = year(sysdate) And month(rt.orderDate) = month(sysdate) And day(rt.orderDate) = day(sysdate) " +
            ")" +
            " GROUP BY so.promotionProgramId " )
    List<PromotionProgramDTO> countDiscountUsed(Long shopId, Long customerId, List<Long> promotionIds);

    @Query("Select new vn.viettel.core.dto.promotion.PromotionProgramDTO(so.promotionCode, COUNT( s.id ))" +
            " From SaleOrder s Join SaleOrderDetail so On s.id = so.saleOrderId " +
            " Where s.shopId =:shopId And s.customerId =:customerId And so.promotionCode IN (:promotionProgramCodes) And so.isFreeItem = 1 And s.type = 1 " +
            " And year(s.orderDate) = year(sysdate) And month(s.orderDate) = month(sysdate) And day(s.orderDate) = day(sysdate) " +
            " And s.id NOT IN ( " +
                " Select DISTINCT rt.fromSaleOrderId From SaleOrder rt Join SaleOrderDetail rtd On rt.id = rtd.saleOrderId " +
                " Where rt.shopId =:shopId And rt.customerId =:customerId And rtd.promotionCode IN (:promotionProgramCodes) And rtd.isFreeItem = 1 And rt.type = 2 " +
                " And year(rt.orderDate) = year(sysdate) And month(rt.orderDate) = month(sysdate) And day(rt.orderDate) = day(sysdate) " +
            ")" +
            " GROUP BY so.promotionCode " )
    List<PromotionProgramDTO> countDiscountUsedFreeItem(Long shopId, Long customerId, List<String> promotionProgramCodes);

    List<SaleOrderDiscount> findAllBySaleOrderId(Long saleOrderId);
}
