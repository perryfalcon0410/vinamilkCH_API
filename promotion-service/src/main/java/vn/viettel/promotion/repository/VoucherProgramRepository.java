package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.voucher.VoucherProgram;
import vn.viettel.core.repository.BaseRepository;

public interface VoucherProgramRepository extends BaseRepository<VoucherProgram>, JpaSpecificationExecutor<VoucherProgram> {
}
