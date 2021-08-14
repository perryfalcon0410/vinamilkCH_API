package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends BaseRepository<Voucher>, JpaSpecificationExecutor<Voucher> {

    @Query("Select v From Voucher v Join VoucherProgram p On v.voucherProgramId = p.id " +
            " Where p.fromDate <=:lastDay And (p.toDate Is Null OR p.toDate >=:firstDay)" +
            " And upper(v.serial) =:serial And v.isUsed = false And v.status = 1 And v.activated = true ")
    Optional<Voucher> getBySerial(String serial, LocalDateTime firstDay, LocalDateTime lastDay);

    @Query(value = "SELECT v FROM Voucher v WHERE v.isUsed = true AND v.saleOrderId = :saleOrderId")
    List<Voucher> getVoucherBySaleOrderId(long saleOrderId);
}
