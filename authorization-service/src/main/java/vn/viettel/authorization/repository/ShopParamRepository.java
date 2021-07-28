package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.ShopParam;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface ShopParamRepository extends BaseRepository<ShopParam> {
    @Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE = 'SALEMT_ONLINE_ORDER' AND CODE = 'VES_EDITING' " +
            "AND STATUS = 1 AND SHOP_ID = :id", nativeQuery = true)
    ShopParam isEditable(Long id);

    @Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE = 'SALEMT_ONLINE_ORDER' AND CODE = 'MANUAL_ORDER' " +
            "AND STATUS = 1 AND SHOP_ID = :id", nativeQuery = true)
    ShopParam isManuallyCreatable(Long id);

//    @Query(value = "SELECT NAME FROM SHOP_PARAM WHERE TYPE = 'SALEMT_TH' AND CODE = 'NTKH' " +
//            "AND STATUS = 1 AND SHOP_ID = :id", nativeQuery = true)
//    String dayReturn(Long id);

    @Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE =:type AND CODE =:code AND SHOP_ID =:shopId AND STATUS = 1", nativeQuery = true)
    Optional<ShopParam> getShopParam(String type, String code, Long shopId);

    /*@Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE = 'SALEMT_LIMIT_DAY_RETURN' AND CODE = 'IMPORT_TRANS_RETURN' AND SHOP_ID =:shopId AND STATUS = 1", nativeQuery = true)
    ShopParam getImportSaleReturn(Long shopId);*/

    @Query(value = "SELECT dta.NAME FROM (\n" +
            "    SELECT sm.NAME, sp.ID, ROW_NUMBER() OVER(ORDER BY sp.LL ASC ) AS RNR\n" +
            "    FROM SHOP_PARAM sm\n" +
            "    JOIN(\n" +
            "        SELECT sp.ID, LEVEL AS LL\n" +
            "        FROM SHOPS sp\n" +
            "        WHERE sp.STATUS = 1\n" +
            "        START WITH sp.ID = :shopId\n" +
            "        CONNECT BY PRIOR PARENT_SHOP_ID = sp.ID\n" +
            "    )sp ON sp.ID = sm.SHOP_ID\n" +
            "    WHERE sm.TYPE = 'SALEMT_LIMIT_DAY_RETURN'\n" +
            "        AND sm.CODE = 'IMPORT_TRANS_RETURN' \n" +
            "        AND sm.STATUS = 1\n" +
            ")dta WHERE dta.RNR = 1", nativeQuery = true)
    String getImportSaleReturn(Long shopId);

    @Query(value = "Select s from ShopParam s Where s.type = 'SALEMT_TH' And s.code = 'NTKH' And s.status = 1 And s.shopId =:shopId ")
    ShopParam dayReturn(Long shopId);
}
