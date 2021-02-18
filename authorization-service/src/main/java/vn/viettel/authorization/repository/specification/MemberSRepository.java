package vn.viettel.authorization.repository.specification;

import vn.viettel.core.db.entity.Member;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberSRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
    List<Member> findAll(@Nullable Specification<Member> spec);
}
