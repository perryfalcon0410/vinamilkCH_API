package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PoTransDetailRepository extends BaseRepository<PoTransDetail> {

    List<PoTransDetail> getPoTransDetailByTransIdAndDeletedAtIsNull(Long id);

    @Query(value = "SELECT * FROM PO_TRANS_DETAIL WHERE TRANS_ID =:transId AND DELETED_AT IS NULL ", nativeQuery = true)
    List<PoTransDetail> getPoTransDetailAndDeleteAtIsNull(Long transId);

    @Query(value = "SELECT ID FROM PO_TRANS_DETAIL WHERE TRANS_ID =:transId AND DELETED_AT IS NULL ", nativeQuery = true)
    List<BigDecimal> getIdByTransId(Long transId);
    @Query(value = "SELECT * FROM PO_TRANS_DETAIL WHERE TRANS_ID =:transId AND PRICE != 0  AND DELETED_AT IS NULL ", nativeQuery = true)
    List<PoTransDetail> getPoTransDetail0(Long transId);

    @Query(value = "SELECT * FROM PO_TRANS_DETAIL WHERE TRANS_ID =:transId AND PRICE = 0  AND DELETED_AT IS NULL ", nativeQuery = true)
    List<PoTransDetail> getPoTransDetail1(Long transId);
}
