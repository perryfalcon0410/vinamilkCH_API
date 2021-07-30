package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoConfirmRepository extends BaseRepository<PoConfirm> {
    @Query(value = "SELECT pc FROM PoConfirm pc WHERE pc.shopId =:shopId AND pc.status = 0 ")
    List<PoConfirm> getPoConfirm(Long shopId);

    @Query(value = "SELECT pc FROM PoConfirm pc WHERE pc.shopId =:shopId AND pc.poNumber = :poNumber ")
    PoConfirm getPoConfirm(Long shopId, String poNumber);

}
