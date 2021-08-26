package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeTransRepository extends BaseRepository<ExchangeTrans>, JpaSpecificationExecutor<ExchangeTrans> {

    @Query(value = "SELECT transCode FROM ExchangeTrans WHERE  status =1 ")
    List<String> getListExChangeCodes();

    @Query(value = "SELECT new vn.viettel.sale.service.dto.ExchangeTotalDTO( sum(ex.quantity) , sum(ex.totalAmount)) " +
            " FROM ExchangeTrans ex WHERE  ( :status is null or ex.status = :status ) " +
            " AND ( :transCode is null or upper(ex.transCode) LIKE %:transCode%) " +
            " AND ( :shopId is null or ex.shopId = :shopId) " +
            " AND ( :reasonId is null or ex.reasonId = :reasonId) " +
            "   AND (:fromDate is null OR ex.transDate >= :fromDate ) " +
            "   AND (:toDate is null OR  ex.transDate <= :toDate ) " +
            "")
    ExchangeTotalDTO getExchangeTotal(Long shopId, String transCode, Integer status, Long reasonId, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "SELECT ex " +
            " FROM ExchangeTrans ex " +
            " WHERE ( :status is null or ex.status = :status ) " +
            "   AND ( :transCode is null or upper(ex.transCode) LIKE %:transCode%) " +
            "   AND ( :shopId is null or ex.shopId = :shopId) " +
            "   AND ( :reasonId is null or ex.reasonId = :reasonId) " +
            "   AND (:fromDate is null OR ex.transDate >= :fromDate ) " +
            "   AND (:toDate is null OR  ex.transDate <= :toDate ) " +
            "")
    Page<ExchangeTrans> getExchangeTrans(Long shopId, String transCode, Integer status, Long reasonId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "SELECT ex " +
            " FROM ExchangeTrans ex " +
            " WHERE ( :status is null or ex.status = :status ) " +
            "   AND ( :transCode is null or upper(ex.transCode) LIKE %:transCode%) " +
            "   AND ( :shopId is null or ex.shopId = :shopId) " +
            "   AND ( :reasonId is null or ex.reasonId = :reasonId) " +
            "   AND (:fromDate is null OR ex.transDate >= :fromDate ) " +
            "   AND (:toDate is null OR  ex.transDate <= :toDate ) " +
            " ORDER BY decode(ex.reasonId, :reasonIds) " +
            "")
    Page<ExchangeTrans> getExchangeTrans(Long shopId, String transCode, Integer status, Long reasonId, LocalDateTime fromDate, LocalDateTime toDate, List<Long> reasonIds, Pageable pageable);

    @Query(value = "SELECT distinct ex.reasonId " +
            " FROM ExchangeTrans ex " +
            " WHERE ( :status is null or ex.status = :status ) " +
            "   AND ( :transCode is null or upper(ex.transCode) LIKE %:transCode%) " +
            "   AND ( :shopId is null or ex.shopId = :shopId) " +
            "   AND ( :reasonId is null or ex.reasonId = :reasonId) " +
            "   AND (:fromDate is null OR ex.transDate >= :fromDate ) " +
            "   AND (:toDate is null OR  ex.transDate <= :toDate ) " +
            "")
    List<Long> getReasonIds(Long shopId, String transCode, Integer status, Long reasonId, LocalDateTime fromDate, LocalDateTime toDate);


}
