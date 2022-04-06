package vn.viettel.sale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.PoAutoGroup;
import vn.viettel.sale.service.dto.poSplitDTO;

public interface PoAutoGroupRepository extends BaseRepository<PoAutoGroup>, JpaSpecificationExecutor<PoAutoGroup> {

    @Query(value = 
    		"SELECT "
    		+ "	   new vn.viettel.sale.service.dto.poSplitDTO "
    		+ "    (pog.id , "
    		+ "    pogd.objectType , "
    		+ "    pogd.objectId ) "
    		+ "FROM "
    		+ "    PoAutoGroup pog "
    		+ "    JOIN PoAutoGroupDetail pogd ON pogd.poAutoGroupoId = pog.id "
    		+ "    LEFT JOIN PoAutoGroupShopMap pogsm ON pogsm.shopId = :shopId "
    		+ "WHERE "
    		+ "    ( pog.poAutoGroupShopMapId IS NULL "
    		+ "      AND pogsm.id IS NULL ) "
    		+ "    OR ( pog.poAutoGroupShopMapId = pogsm.id "
    		+ "         AND pogsm.id IS NOT NULL )")
    List<poSplitDTO> getSplitPO(Long shopId);
}
