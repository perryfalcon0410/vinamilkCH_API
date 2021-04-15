package vn.viettel.report.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.Product_;

import java.util.List;

public class ProductsSpecification {
    public static Specification<Product> hasProductCode(List<String> productCode) {
        return (root, query, criteriaBuilder) -> {
            if (productCode == null) {
                return criteriaBuilder.conjunction();
            }
            return root.get(Product_.productCode).in(productCode);
        };
    }
}
