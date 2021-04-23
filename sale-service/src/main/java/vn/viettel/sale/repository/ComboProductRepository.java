package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.core.repository.BaseRepository;

public interface ComboProductRepository extends BaseRepository<ComboProduct>, JpaSpecificationExecutor<ComboProduct> {
}
