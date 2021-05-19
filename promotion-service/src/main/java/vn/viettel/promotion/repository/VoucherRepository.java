package vn.viettel.promotion.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.Voucher;

import java.util.List;

public interface VoucherRepository extends BaseRepository<Voucher>, JpaSpecificationExecutor<Voucher> {

    // find vouchers for sale
    @Query( value = "SELECT * FROM VOUCHERS v " +
            "WHERE ( v.VOUCHER_NAME =:keyWord OR v.VOUCHER_CODE =:keyWord OR v.SERIAL =:keyWord ) " +
            "AND v.IS_USED = 0 AND v.STATUS = 1 "
        , nativeQuery = true
    )
    Page<Voucher> findVouchers(String keyWord, Pageable pageable);

    @Query(value = "SELECT * FROM VOUCHERS WHERE IS_USED = 1 AND SALE_ORDER_ID = :ID", nativeQuery = true)
    List<Voucher> getVoucherBySaleOrderId(long ID);
}
