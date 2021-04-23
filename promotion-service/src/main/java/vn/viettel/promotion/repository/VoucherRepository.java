package vn.viettel.promotion.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.Voucher;

import java.util.Date;
import java.util.List;

public interface VoucherRepository extends BaseRepository<Voucher>, JpaSpecificationExecutor<Voucher> {

    // find vouchers for sale
    @Query( value = "SELECT * FROM VOUCHERS v " +
        "LEFT JOIN VOUCHER_PROGRAM p ON p.ID = v.VOUCHER_PROGRAM_ID " +
        "WHERE (" +
            "p.VOUCHER_PROGRAM_NAME LIKE %:keyWord% OR p.PROGRAM_NAME_TEXT LIKE %:keyAccent% OR " +
            "v.VOUCHER_NAME LIKE %:keyWord% OR v.VOUCHER_NAME_TEXT LIKE %:keyAccent% OR " +
            "v.VOUCHER_CODE LIKE %:keyWord% OR v.SERIAL LIKE %:keyWord%) " +
        "AND v.IS_USED = 0 AND v.STATUS = 1 AND v.DELETED_AT IS NULL"
        , nativeQuery = true
    )
    Page<Voucher> findVouchers(String keyWord, String keyAccent, Pageable pageable);

    @Query(value = "SELECT * FROM VOUCHERS WHERE IS_USED = 1 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<Voucher> getVoucherBySaleOrderId(long ID);
}
