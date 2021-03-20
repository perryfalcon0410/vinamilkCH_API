package vn.viettel.common.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.Area;
import vn.viettel.core.db.entity.Area_;

public final class AreaSpecification {
    public static Specification<Area> hasName(String searchKeywords) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Area_.name), "%" + searchKeywords + "%");
    }
}
