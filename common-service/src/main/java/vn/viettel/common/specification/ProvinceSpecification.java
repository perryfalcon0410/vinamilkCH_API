package vn.viettel.common.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.Province;
import vn.viettel.core.db.entity.Province_;


public final class ProvinceSpecification {
    public static Specification<Province> hasName(String searchKeywords) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Province_.name), "%" + searchKeywords + "%");
    }
}
