package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.Shop;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends BaseRepository<Shop> {

    Optional<Shop> findByIdAndStatus(Long id, Integer status);

    Shop findByShopName(String name);

    Shop findByShopCode(String code);

    @Query(value = "SELECT s FROM Shop s WHERE s.id IN (:ids)")
    List<Shop> getShopByIds(List<Long> ids);

    List<Shop> findByParentShopIdAndShopTypeAndStatus(Long parentId, String type, Integer status);

//    @Query(value = "SELECT dta.NAME FROM (\n" +
//            "    SELECT sm.NAME, sp.ID, ROW_NUMBER() OVER(ORDER BY sp.LL DESC) AS RNR\n" +
//            "    FROM SHOP_PARAM sm\n" +
//            "    JOIN(\n" +
//            "        SELECT sp.ID, LEVEL AS LL\n" +
//            "        FROM SHOPS sp\n" +
//            "        WHERE sp.STATUS = 1\n" +
//            "        START WITH sp.ID = :shopId\n" +
//            "        CONNECT BY PRIOR PARENT_SHOP_ID = sp.ID\n" +
//            "    )sp ON sp.ID = sm.SHOP_ID\n" +
//            "    WHERE sm.TYPE = 'SALEMT_EDIT_CUSTOMER'\n" +
//            "        AND sm.CODE = 'OTHER_STORE' \n" +
//            "        AND sm.STATUS = 1\n" +
//            ")dta WHERE dta.RNR = 1", nativeQuery = true)
//    String getLevelUpdateCustomer(int shopId);
}
