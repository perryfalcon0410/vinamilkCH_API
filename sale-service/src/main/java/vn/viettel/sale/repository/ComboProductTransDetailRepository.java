package vn.viettel.sale.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ComboProductTransDetail;

import java.util.List;

public interface ComboProductTransDetailRepository extends BaseRepository<ComboProductTransDetail> {

    List<ComboProductTransDetail> findByTransId(Long transId);
}
