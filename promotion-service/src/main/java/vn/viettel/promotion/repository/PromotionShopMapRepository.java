package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionShopMap;

import java.util.List;

public interface PromotionShopMapRepository extends BaseRepository<PromotionShopMap> {

//    @Query(value = "SELECT p FROM PromotionShopMap p " +
//            "WHERE p.promotionProgramId =:programId AND p.shopId =:shopId AND p.fromDate <=:lastDay AND (p.toDate IS NULL OR p.toDate >=:firstDay) AND p.status = 1 ")
    @Query(value = "SELECT p FROM PromotionShopMap p " +
            "WHERE p.promotionProgramId =:programId AND p.shopId =:shopId AND p.status = 1 ")
    List<PromotionShopMap> findByPromotionProgramIdAndShopId(Long programId, Long shopId);


    @Query(value = "SELECT p FROM PromotionShopMap p " +
            " Join PromotionProgram  pp On pp.id = p.promotionProgramId " +
            "WHERE pp.promotionProgramCode =:code AND p.shopId =:shopId AND p.status = 1 ")
   PromotionShopMap findByPromotionProgramCode(String code, Long shopId);

}
