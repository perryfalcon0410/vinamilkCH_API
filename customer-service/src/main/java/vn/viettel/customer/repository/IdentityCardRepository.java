package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.IdentityCard;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface IdentityCardRepository extends BaseRepository<IdentityCard>, JpaSpecificationExecutor<IdentityCard> {

    Optional<IdentityCard> getIdentityCardByIdentityCardCode(String code);

    IdentityCard getIdentityCardByIdAndDeletedAtIsNull(Long id);
}
