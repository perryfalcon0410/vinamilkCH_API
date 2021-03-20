package vn.viettel.common.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.Ward;
import vn.viettel.core.db.entity.Ward_;

public final class WardSpecification {
    public static Specification<Ward> hasName(String searchKeywords) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Ward_.name), "%" + searchKeywords + "%");
    }
}
