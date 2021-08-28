package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoConfirmRepository extends BaseRepository<PoConfirm> {
    @Query(value = "SELECT pc FROM PoConfirm pc WHERE pc.shopId =:shopId AND pc.status = 0 " +
            " ORDER BY pc.orderDate desc , pc.poNumber desc ")
    List<PoConfirm> getPoConfirm(Long shopId);

    @Query(value = "SELECT pc FROM PoConfirm pc WHERE pc.shopId =:shopId AND pc.poCoNumber = :poCoNumber AND pc.internalNumber =:internalNumber")
    PoConfirm getPoConfirm(Long shopId, String poCoNumber, String internalNumber) ;

}
