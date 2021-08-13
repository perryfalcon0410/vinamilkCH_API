package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.VoucherProgram;

import java.util.Optional;

public interface VoucherProgramRepository extends BaseRepository<VoucherProgram> {

}
