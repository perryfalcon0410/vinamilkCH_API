package vn.viettel.sale.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface WareHouseTypeRepository extends BaseRepository<WareHouseType> {

    /*
            Đồng bộ PO confirm lấy mặc định 1 loại kho order by code asc
         */
    @Query(value = "SELECT w FROM WareHouseType w WHERE w.status = 1 ORDER BY w.wareHouseTypeCode asc ")
    List<WareHouseType> findDefault();
}
