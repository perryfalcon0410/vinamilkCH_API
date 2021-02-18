package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Distributor;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DistributorRepository extends BaseRepository<Distributor> {
    Optional<Distributor> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<Distributor> findByDistributorNumberAndDeletedAtIsNull(String distributorNumber);

    Distributor findByIdAndDeletedAtIsNull(Long id);

    @Query(value = "SELECT d.* FROM user_services.distributors d "
            + "JOIN user_services.users u ON d.user_id = u.id "
            + "WHERE email LIKE %:email% AND d.deleted_at is null ", nativeQuery = true)
    List<Distributor> getByEmail(@Param("email") String email);

    @Query(value = "SELECT d.* FROM user_services.distributors d "
            + "JOIN user_services.users u ON d.user_id = u.id "
            + "WHERE name LIKE %:name% AND d.deleted_at is null ", nativeQuery = true)
    List<Distributor> getByName(@Param("name") String name);
}
