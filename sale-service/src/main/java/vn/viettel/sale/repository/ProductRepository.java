package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.db.entity.stock.StockAdjustment;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {
    Product getProductByProductCode(String productCode);
}
