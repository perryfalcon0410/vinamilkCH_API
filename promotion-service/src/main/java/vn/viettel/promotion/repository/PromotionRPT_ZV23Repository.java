package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.RPT_ZV23;

import java.util.List;
import java.util.Set;

public interface PromotionRPT_ZV23Repository extends BaseRepository<RPT_ZV23> {

    @Query(value = "SELECT zv FROM RPT_ZV23 zv" +
            " JOIN PromotionProgram pr ON pr.id = zv.promotionProgramId" +
            " WHERE zv.promotionProgramCode =:promotionCode" +
            " AND zv.customerId =:customerId" +
            " AND zv.shopId =:shopId" +
            " AND pr.status = 1")
    RPT_ZV23 checkZV23Require(String promotionCode, Long customerId, Long shopId);


    @Query(value = "SELECT zv FROM RPT_ZV23 zv" +
            " JOIN PromotionProgram pr ON pr.id = zv.promotionProgramId" +
            " WHERE zv.promotionProgramCode =:promotionCode" +
            " AND zv.customerId =:customerId" +
            " AND pr.status = 1" +
            " ORDER BY zv.updatedAt desc ")
    List<RPT_ZV23> checkZV23Require(String promotionCode, Long customerId);

    @Query("Select r From RPT_ZV23 r Where r.promotionProgramId In (:programIds) And r.customerId =:customerId And r.shopId = :shopId ")
    List<RPT_ZV23> getByProgramIds(Set<Long> programIds, Long customerId, Long shopId);

}