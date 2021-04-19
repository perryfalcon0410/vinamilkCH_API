package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.common.ShopParam;
import vn.viettel.core.repository.BaseRepository;

public interface ShopParamRepository extends BaseRepository<ShopParam> {
    @Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE = 'SALEMT_ONLINE_ORDER' AND CODE = 'VES_EDITING' " +
            "AND STATUS = 1 AND SHOP_ID = :id", nativeQuery = true)
    ShopParam isEditable(Long id);

    @Query(value = "SELECT * FROM SHOP_PARAM WHERE TYPE = 'SALEMT_ONLINE_ORDER' AND CODE = 'MANUAL_ORDER' " +
            "AND STATUS = 1 AND SHOP_ID = :id", nativeQuery = true)
    ShopParam isManuallyCreatable(Long id);
}
