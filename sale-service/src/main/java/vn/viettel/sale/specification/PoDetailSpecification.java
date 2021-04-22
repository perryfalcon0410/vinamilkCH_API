package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.sale.entities.PoDetail_;

public class PoDetailSpecification {
    public static Specification<PoDetail> hashPoId(Long poId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoDetail_.poId), poId);

    }
}
