package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ExchangeTransRepository extends BaseRepository<ExchangeTrans>, JpaSpecificationExecutor<ExchangeTrans> {
    @Query(value = "SELECT transCode FROM ExchangeTrans WHERE  status =1 ")
    List<String> getListExChangeCodes();
}
