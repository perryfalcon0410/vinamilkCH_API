package vn.viettel.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.PrinterConfig;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface PrinterConfigRepository  extends BaseRepository<PrinterConfig> {

    @Query(value = "Select p FROM PrinterConfig p Where p.clientIp =:clientIp Order By p.updatedAt desc NULLS LAST" )
    Page<PrinterConfig> getPrinterConfigs(String clientIp, Pageable pageable);
}
