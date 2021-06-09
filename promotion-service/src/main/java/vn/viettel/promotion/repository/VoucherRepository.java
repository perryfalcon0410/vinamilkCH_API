package vn.viettel.promotion.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends BaseRepository<Voucher>, JpaSpecificationExecutor<Voucher> {

    Optional<Voucher> getByIdAndStatusAndIsUsed(Long id, Integer status, Boolean isUse);

    @Query( value = "SELECT * FROM VOUCHERS v " +
            "JOIN VOUCHER_PROGRAM p ON v.VOUCHER_PROGRAM_ID = p.ID " +
            "WHERE p.STATUS = 1 " +
            "AND (p.TO_DATE IS NULL OR (p.FROM_DATE <=:fromDate AND p.TO_DATE >=:toDate)) " +
            "AND (v.VOUCHER_CODE =:keyWord OR v.SERIAL =:keyWord) " +
            "AND v.IS_USED = 0 AND v.STATUS = 1 "
            , nativeQuery = true
    )
    Optional<Voucher> getByVoucherCode(String keyWord, LocalDateTime fromDate, LocalDateTime toDate);

    // find vouchers for sale
    @Query( value = "SELECT * FROM VOUCHERS v " +
            "JOIN VOUCHER_PROGRAM p ON v.VOUCHER_PROGRAM_ID = p.ID " +
            "WHERE p.STATUS = 1 " +
            "AND (p.TO_DATE IS NULL OR (p.FROM_DATE <=:fromDate AND p.TO_DATE >=:toDate)) " +
            "AND (v.VOUCHER_NAME =:keyWord OR v.VOUCHER_CODE =:keyWord OR v.SERIAL =:keyWord ) " +
            "AND v.IS_USED = 0 AND v.STATUS = 1 "
        , nativeQuery = true
    )
    Page<Voucher> findVouchers(String keyWord, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "SELECT * FROM VOUCHERS WHERE IS_USED = 1 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<Voucher> getVoucherBySaleOrderId(long ID);
}
