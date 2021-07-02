package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ComboProductTrans;
import vn.viettel.sale.service.dto.TotalDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ComboProductTransRepository extends BaseRepository<ComboProductTrans>, JpaSpecificationExecutor<ComboProductTrans> {

    @Query(value = "SELECT ct.transCode FROM ComboProductTrans ct WHERE ct.transCode LIKE :startWith% AND ct.shopId =:shopId "
            + " ORDER BY ct.transCode DESC ")
    List<String> getTransCodeTop1(Long shopId, String startWith);

    @Query(value = "SELECT new vn.viettel.sale.service.dto.TotalDTO( sum(ex.totalQuantity), sum (ex.totalAmount)) " +
            " FROM ComboProductTrans ex WHERE ( :transType is null or ex.transType = :transType ) " +
            " AND ( :transCode is null or ex.transCode = :transCode) " +
            " AND ( :shopId is null or ex.shopId = :shopId) " +
            " AND (ex.transDate is null OR (:fromDate is null AND :toDate is null) OR (:fromDate is null AND ex.transDate <= :toDate ) OR (:toDate is null AND :fromDate <= ex.transDate ) OR (ex.transDate BETWEEN :fromDate AND :toDate) ) " +
            "")
    TotalDTO getExchangeTotal(Long shopId, String transCode, Integer transType, LocalDateTime fromDate, LocalDateTime toDate);
}
