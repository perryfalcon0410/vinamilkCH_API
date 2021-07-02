package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PoTransDetailRepository extends BaseRepository<PoTransDetail> {

    List<PoTransDetail> getPoTransDetailByTransId(Long id);

    @Query(value = "SELECT pd FROM PoTransDetail pd WHERE pd.transId =:transId  ")
    List<PoTransDetail> getPoTransDetail(Long transId);

    @Query(value = "SELECT pd FROM PoTransDetail pd WHERE pd.transId =:transId AND pd.price != 0  ")
    List<PoTransDetail> getPoTransDetail0(Long transId);

    @Query(value = "SELECT pd FROM PoTransDetail pd WHERE pd.transId =:transId AND pd.price = 0 ")
    List<PoTransDetail> getPoTransDetail1(Long transId);
}
