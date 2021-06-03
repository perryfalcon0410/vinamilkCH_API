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

    @Query(value = "SELECT NAME FROM SHOP_PARAM WHERE TYPE = 'SALEMT_TH' AND CODE = 'NTKH' " +
            "AND STATUS = 1 AND SHOP_ID = :id", nativeQuery = true)
    String dayReturn(Long id);

    @Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE =:type AND CODE =:code AND SHOP_ID =:shopId AND STATUS = 1", nativeQuery = true)
    Optional<ShopParam> getShopParam(String type, String code, Long shopId);

    @Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE = 'SALEMT_LIMIT_DAY_RETURN' AND CODE = 'IMPORT_TRANS_RETURN' AND SHOP_ID =:shopId AND STATUS = 1", nativeQuery = true)
    ShopParam getImportSaleReturn(Long shopId);
}
