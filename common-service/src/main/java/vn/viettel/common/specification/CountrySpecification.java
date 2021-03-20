package vn.viettel.common.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.Country;
import vn.viettel.core.db.entity.Country_;


public final class CountrySpecification {
    public static Specification<Country> hasName(String searchKeywords) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Country_.name), "%" + searchKeywords + "%");
    }
}
