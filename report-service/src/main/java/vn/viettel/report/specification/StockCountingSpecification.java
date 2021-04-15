package vn.viettel.report.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.db.entity.stock.StockCountingDetail_;

import java.util.Date;

public class StockCountingSpecification {

    public static Specification<StockCountingDetail> hasProductId(Long productId) {

        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(StockCountingDetail_.productId), productId);
        };
    }

    public static Specification<StockCountingDetail> hasCountingDate(Date countingDate) {
        return (root, query, criteriaBuilder) -> {
            if (countingDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(StockCountingDetail_.countingDate), countingDate);
        };
    }
}
