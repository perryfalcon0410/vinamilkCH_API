package vn.viettel.saleservice.repository;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.WareHouse;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface WareHouseRepository extends BaseRepository <WareHouse>{
    WareHouse findByAddressAndWarehouse_name(String name, String address);
}
