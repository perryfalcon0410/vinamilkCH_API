package vn.viettel.sale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;

public interface WareHouseTypeRepository extends BaseRepository<WareHouseType> {

    /*
     Đồng bộ PO confirm lấy mặc định 1 loại kho order by code asc
     */
    @Query(value = "SELECT w FROM WareHouseType w WHERE w.status = 1 ORDER BY w.wareHouseTypeCode asc ")
    List<WareHouseType> findDefault();

    @Query(value = "SELECT new vn.viettel.sale.service.dto.WareHouseTypeDTO( w.id, w.wareHouseTypeCode, w.wareHouseTypeName, Case when w.id =:defaultId then 1 else 0 end )" +
            " FROM WareHouseType w WHERE w.status = 1 ORDER BY w.wareHouseTypeCode asc ")
    List<WareHouseTypeDTO> findWithDefault(Long defaultId);
    
    @Query(value = "select wh from WareHouseType wh where wh.wareHouseTypeCode = :code")
    WareHouseType getByCode(String code);
}
