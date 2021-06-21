package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends BaseRepository<Voucher>, JpaSpecificationExecutor<Voucher> {

//    @Query( value = "SELECT * FROM VOUCHERS v " +
//            "JOIN VOUCHER_PROGRAM p ON v.VOUCHER_PROGRAM_ID = p.ID " +
//            "WHERE p.STATUS = 1 " +
//            "AND (p.TO_DATE IS NULL OR (p.FROM_DATE <=:fromDate AND p.TO_DATE >=:toDate)) " +
//            "AND v.SERIAL =:serial " +
//            "AND v.ACTIVATED = 1 AND v.IS_USED = 0 AND v.STATUS = 1 "
//            , nativeQuery = true
//    )
//    Optional<Voucher> getBySerial(String serial, LocalDateTime fromDate, LocalDateTime toDate);


    @Query("Select v From Voucher v Join VoucherProgram p On p.id = v.voucherProgramId " +
            "Where p.status = 1 " +
            "And v.serial =:serial AND v.activated = true And v.isUsed = false And v.status = 1 ")
    Optional<Voucher> getBySerial(String serial);

    @Query(value = "SELECT * FROM VOUCHERS WHERE IS_USED = 1 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<Voucher> getVoucherBySaleOrderId(long ID);
}
