package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.PoTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoTransDetailRepository extends BaseRepository<PoTransDetail> {

   /* List<PoTransDetail> getPoTransDetailByTransIdAndDeletedAtIsNull(Long id);*/
    List<PoTransDetail> getPoTransDetailByTransId(Long id);
}
