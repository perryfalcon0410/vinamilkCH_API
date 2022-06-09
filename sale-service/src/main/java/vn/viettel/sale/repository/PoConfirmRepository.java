package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PoConfirmRepository extends BaseRepository<PoConfirm> {
    @Query(value = "SELECT pc FROM PoConfirm pc WHERE pc.shopId =:shopId AND pc.status = 0 " +
            " ORDER BY pc.orderDate desc , pc.poNumber desc ")
    List<PoConfirm> getPoConfirm(Long shopId);

    @Query(value = "SELECT pc FROM PoConfirm pc WHERE pc.shopId =:shopId AND pc.poCoNumber = :poCoNumber AND pc.internalNumber =:internalNumber")
    PoConfirm getPoConfirm(Long shopId, String poCoNumber, String internalNumber) ;

    @Query(value = "select pod.quantity "
    		+ "from PoConfirm poc "
    		+ "join PoDetail pod "
    		+ "on poc.id = pod.poId "
    		+ "where poc.shopId = :shopId and "
    		+ "poc.status = :status and "
    		+ "poc.importDate between :fromDate and :toDate ")
    Integer getQuantityByShopIdAndStatusAndImportDate(Long shopId, int status, LocalDateTime fromDate, LocalDateTime toDate); 
}
