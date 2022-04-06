package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.Price;

public interface PriceRepository extends BaseRepository<Price>, JpaSpecificationExecutor<Price> {

    @Query(value = "select * from prices where (product_id, id) in ( "
    		+ "    select product_id, min(id) "
    		+ "    from prices where (product_id, from_date) in ( "
    		+ "        select product_id, max(from_date) "
    		+ "        from prices where status = 1 group by product_id) "
    		+ "    group by product_id) and product_id = ?1"
    		, nativeQuery = true)
    Price getNewPriceOfProduct(Long productId);
}
