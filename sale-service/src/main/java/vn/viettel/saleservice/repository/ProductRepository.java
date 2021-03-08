package vn.viettel.saleservice.repository;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface ProductRepository extends BaseRepository<Product> {
    Product findProductByProductCode(String productcode);
}
