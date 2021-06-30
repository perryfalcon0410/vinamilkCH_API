package vn.viettel.sale.repository;

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

    @Query(value = "SELECT new vn.viettel.sale.service.dto.ExchangeTotalDTO( sum(ex.quantity) , sum (ex.totalAmount)) " +
            " FROM ExchangeTrans ex WHERE  ( :status is null or ex.status = :status ) " +
            " AND ( :transCode is null or ex.transCode = :transCode) " +
            " AND ( :shopId is null or ex.shopId = :shopId) " +
            " AND ( :reasonId is null or ex.reasonId = :reasonId) " +
            " AND ( (:fromDate is null AND :toDate is null) OR (:fromDate is null AND ex.transDate <= :toDate ) OR (:toDate is null AND :fromDate <= ex.transDate ) OR (ex.transDate BETWEEN :fromDate AND :toDate) ) " +
            "")
    ExchangeTotalDTO getExchangeTotal(Long shopId, String transCode, Integer status, Long reasonId, LocalDateTime fromDate, LocalDateTime toDate);
}
