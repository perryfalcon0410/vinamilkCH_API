package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.VoucherProgram;

import java.util.Optional;

public interface VoucherProgramRepository extends BaseRepository<VoucherProgram> {

    @Query("Select p From VoucherProgram p " +
            "WHERE p.fromDate <= sysdate And (p.toDate Is Null OR sysdate <= p.toDate) And p.status = 1 ")
    Optional<VoucherProgram> getVoucherProgram(Long id);
}
