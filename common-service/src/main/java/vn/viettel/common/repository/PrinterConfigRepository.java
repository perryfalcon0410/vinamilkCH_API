package vn.viettel.common.repository;

import vn.viettel.common.entities.PrinterConfig;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface PrinterConfigRepository  extends BaseRepository<PrinterConfig> {

    Optional<PrinterConfig> getByClientIp(String clientIp);
}
