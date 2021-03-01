package vn.viettel.core.db.entity.specification;

import vn.viettel.core.db.entity.Member;
import vn.viettel.core.db.entity.Member_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.util.Optional;

public final class MemberSpecification {
    /**
     * This function act as LIKE
     * @param idLike
     * @return
     */
    public static Specification<Member> hasIdLike(Optional<String> idLike) {
        return (root, query, builder) -> {
            Path<Long> idField = root.get(Member_.id);
            Expression<String> function = builder.function("LPAD", String.class,
                    idField, builder.literal(10), builder.literal("0"));
            return idLike.map(item -> builder
                    .like(function, "%" + String.valueOf(item) + "%"))
                    .orElse(null);
        };
    }


}
