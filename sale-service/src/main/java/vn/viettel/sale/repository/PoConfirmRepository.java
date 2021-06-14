package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoConfirmRepository extends BaseRepository<PoConfirm> {
    @Query(value = "SELECT * FROM PO_CONFIRM WHERE SHOP_ID =:shopId AND STATUS = 0 ", nativeQuery = true)
    List<PoConfirm> getPoConfirm(Long shopId);
}
