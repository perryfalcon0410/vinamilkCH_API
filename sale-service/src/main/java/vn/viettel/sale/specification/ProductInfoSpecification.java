package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.db.entity.common.ProductInfo_;

public class ProductInfoSpecification {

    public static Specification<ProductInfo> hasStatus(Integer status) {

        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ProductInfo_.status), status);
        };
    }

    public static Specification<ProductInfo> hasType(Integer type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ProductInfo_.type), type);
        };
    }

}
