package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.ShopParam;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface ShopParamRepository extends BaseRepository<ShopParam> {

    @Query(value = "SELECT sp FROM ShopParam sp WHERE sp.type = 'SALEMT_ONLINE_ORDER' AND sp.code = 'VES_EDITING' " +
            "AND sp.status = 1 AND sp.shopId = :shopId")
    ShopParam isEditable(Long shopId);

    @Query(value = "SELECT sp FROM ShopParam sp WHERE sp.type = 'SALEMT_ONLINE_ORDER' AND sp.code = 'MANUAL_ORDER' " +
            "AND sp.status = 1 AND sp.shopId = :shopId")
    ShopParam isManuallyCreatable(Long shopId);

    @Query(value = "SELECT sp FROM ShopParam sp WHERE sp.type =:type AND sp.code =:code AND sp.shopId =:shopId AND sp.status = 1")
    Optional<ShopParam> getShopParam(String type, String code, Long shopId);

//    @Query(value = "SELECT dta.NAME FROM (\n" +
//            "    SELECT sm.NAME, sp.ID, ROW_NUMBER() OVER(ORDER BY sp.LL ASC ) AS RNR\n" +
//            "    FROM SHOP_PARAM sm\n" +
//            "    JOIN(\n" +
//            "        SELECT sp.ID, LEVEL AS LL\n" +
//            "        FROM SHOPS sp\n" +
//            "        WHERE sp.STATUS = 1\n" +
//            "        START WITH sp.ID = :shopId\n" +
//            "        CONNECT BY PRIOR PARENT_SHOP_ID = sp.ID\n" +
//            "    )sp ON sp.ID = sm.SHOP_ID\n" +
//            "    WHERE sm.TYPE = 'SALEMT_LIMIT_DAY_RETURN'\n" +
//            "        AND sm.CODE = 'IMPORT_TRANS_RETURN' \n" +
//            "        AND sm.STATUS = 1\n" +
//            ")dta WHERE dta.RNR = 1", nativeQuery = true)
//    String getImportSaleReturn(Long shopId);

    @Query(value = "Select s from ShopParam s Where s.type = :type And s.code = :code And s.status = 1 And s.shopId =:shopId ")
    ShopParam dayReturn(Long shopId, String type, String code);
}
