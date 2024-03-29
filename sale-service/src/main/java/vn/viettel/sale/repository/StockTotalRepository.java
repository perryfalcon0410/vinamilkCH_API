package vn.viettel.sale.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;


public interface StockTotalRepository extends BaseRepository<StockTotal> {

    StockTotal findByProductIdAndWareHouseTypeIdAndShopId(Long productId,Long wareHouseTypeId,Long shopId);

    @Query(value = "SELECT s FROM StockTotal s JOIN Product p ON s.productId = p.id WHERE p.status = 1 AND s.wareHouseTypeId = :wareHouseTypeID " +
            "ORDER BY s.productId asc")
    Page<StockTotal> findAll(Pageable pageable, Long wareHouseTypeID);

    @Query(value = "SELECT s FROM StockTotal s WHERE s.shopId =:shopId " +
            "AND s.wareHouseTypeId =:wareHouseTypeId AND s.productId IN :productIds AND s.status = 1 ")
    List<StockTotal> getStockTotal(Long shopId, Long wareHouseTypeId, List<Long> productIds);

    //kiểm kê
    @Query(value = "SELECT new vn.viettel.sale.service.dto.StockCountingDetailDTO(p.id, p.productCode, p.productName, gcat.productInfoName, " +
            " cat.productInfoName, cat.productInfoCode, s.quantity, p.uom1, p.uom2, p.convFact) " +
            " FROM Product p " +
            " LEFT JOIN ProductInfo gcat ON p.groupCatId = gcat.id and gcat.type = 6 and gcat.status = 1 " +
            " LEFT JOIN ProductInfo cat ON p.catId = cat.id and cat.type = 1 and cat.status = 1 " +
            " JOIN StockTotal s ON s.productId = p.id AND (:wareHouseTypeId Is NULL OR s.wareHouseTypeId = :wareHouseTypeId) AND s.shopId =:shopId AND s.status = 1 " +
            " WHERE (:searchKeywords is null OR p.productNameText LIKE %:searchKeywords% OR upper(p.productCode) LIKE %:searchKeywords%) " +
            " AND p.status = 1 " +
            " ORDER BY cat.productInfoCode asc, p.productCode asc, p.productName asc")
    List<StockCountingDetailDTO> getStockCountingDetail(Long shopId, Long wareHouseTypeId, String searchKeywords);

    @Query(value = "SELECT s FROM StockTotal s WHERE s.shopId =:shopId " +
            "AND s.wareHouseTypeId =:wareHouseTypeId  AND s.status = 1 order by s.updatedAt ")
    List<StockTotal> getStockTotal(Long shopId, Long wareHouseTypeId);

    @Query(value = "SELECT s.quantity FROM StockTotal s WHERE s.shopId =:shopId " +
            "AND s.wareHouseTypeId =:wareHouseTypeId AND s.productId = :productId AND s.status = 1 ")
    Integer getStockTotalByShopProduct(Long shopId, Long wareHouseTypeId, Long productId);
    
}
