package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends BaseRepository<Voucher>, JpaSpecificationExecutor<Voucher> {

    @Query("Select v From Voucher v Join VoucherProgram p On p.id = v.voucherProgramId " +
            "Where p.status = 1 " +
            "And v.serial =:serial AND v.activated = true And v.isUsed = false And v.status = 1 ")
    Optional<Voucher> getBySerial(String serial);

    @Query(value = "SELECT * FROM VOUCHERS WHERE IS_USED = 1 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<Voucher> getVoucherBySaleOrderId(long ID);
}
