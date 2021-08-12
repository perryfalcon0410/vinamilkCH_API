package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.UserLogOnTime;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;

public interface UserLogRepository extends BaseRepository<UserLogOnTime> {

    UserLogOnTime findFirstByOrderByIdDesc();

    @Modifying
    @Query(value = "Insert into USER_LOG_ON_TIME (ID, LOG_CODE, SHOP_ID, ACCOUNT, COMPUTER_NAME, MAC_ADDRESS, CREATED_BY, UPDATED_BY, CREATED_AT, UPDATED_AT ) " +
            "VALUES (:id, :logCode, :shopId, :account, :computerName, :macAddress, :createdBy, :createdBy, :createdAt, :createdAt )", nativeQuery = true)
    int createBeforToken(Long id, String logCode, Long shopId, String account, String computerName, String macAddress, String createdBy, LocalDateTime createdAt);
}
