package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface VoucherRepository extends BaseRepository<Voucher>, JpaSpecificationExecutor<Voucher> {

    // find vouchers for sale
    @Query( value = "SELECT v.* FROM VOUCHERS v " +
        "INNER JOIN VOUCHER_SHOP_MAP s ON v.VOUCHER_PROGRAM_ID = s.VOUCHER_PROGRAM_ID " +
        "INNER JOIN VOUCHER_CUSTOMER_MAP c ON v.VOUCHER_PROGRAM_ID = c.VOUCHER_PROGRAM_ID " +
        "WHERE (v.VOUCHER_CODE LIKE %:keyWord% OR v.VOUCHER_NAME LIKE %:keyWord% OR v.SERIAL LIKE %:keyWord%) " +
        "AND s.SHOP_ID =:shopId AND c.CUSTOMER_TYPE_ID =:customerTypeId"
        , nativeQuery = true
    )
    List<Voucher> findVouchers(
        @Param("keyWord") String keyWord, @Param("shopId") Long shopId, @Param("customerTypeId") Long customerTypeId
    );
}
