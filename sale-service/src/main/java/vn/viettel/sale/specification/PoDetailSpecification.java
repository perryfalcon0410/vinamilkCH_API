package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.PoDetail;

public class PoDetailSpecification {
    public static Specification<PoDetail> hashPoId(Long poId) {
       /* return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoDetail_.poId), poId);*/
        return null;
    }
}
