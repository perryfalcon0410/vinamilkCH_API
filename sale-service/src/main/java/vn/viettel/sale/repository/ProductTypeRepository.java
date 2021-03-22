package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.repository.BaseRepository;

public interface ProductTypeRepository extends BaseRepository<ProductType> {
    Page<ProductType> findAll(Pageable pageable);
}
