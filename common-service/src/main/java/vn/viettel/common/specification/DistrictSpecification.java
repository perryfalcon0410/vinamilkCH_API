package vn.viettel.common.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.District;
import vn.viettel.core.db.entity.District_;

public final class DistrictSpecification {
    public static Specification<District> hasName(String searchKeywords) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(District_.name), "%" + searchKeywords + "%");
    }
}
