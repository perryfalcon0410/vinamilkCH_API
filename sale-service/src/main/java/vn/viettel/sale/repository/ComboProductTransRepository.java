package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ComboProductTrans;

public interface ComboProductTransRepository extends BaseRepository<ComboProductTrans>, JpaSpecificationExecutor<ComboProductTrans> {

}
