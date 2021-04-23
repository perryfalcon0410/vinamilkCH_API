package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ComboProductTrans;

public interface ComboProductTransRepository extends BaseRepository<ComboProductTrans>, JpaSpecificationExecutor<ComboProductTrans> {

    @Query(value = "SELECT TRANS_CODE FROM combo_product_trans WHERE TRANS_CODE LIKE :startWith% AND SHOP_ID =:shopId AND DELETED_AT IS NULL "
            + "ORDER BY trans_code DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    String getTransCodeTop1(Long shopId, String startWith);

}
