package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;

import java.util.List;
import java.util.Optional;


public interface StockTotalRepository extends BaseRepository<StockTotal> {
    StockTotal findByProductIdAndWareHouseTypeId(Long productId,Long wareHouseTypeId);

    @Query(value = "SELECT s FROM StockTotal s JOIN Product p ON s.productId = p.id WHERE p.status = 1 AND s.wareHouseTypeId = :wareHouseTypeID " +
            "ORDER BY s.productId asc")
    Page<StockTotal> findAll(Pageable pageable, Long wareHouseTypeID);

    @Query(value = "SELECT s FROM StockTotal s JOIN Product p ON s.productId = p.id WHERE p.status = 1 AND s.wareHouseTypeId = :wareHouseTypeID " +
            "AND (:searchKeywords is null OR p.productName LIKE %:searchKeywords% OR p.productCode LIKE %:searchKeywords%) ORDER BY s.productId asc")
    List<StockTotal> findAllByCodeOrNameProduct(Long wareHouseTypeID, String searchKeywords);

    @Query(value = "SELECT * FROM STOCK_TOTAL WHERE SHOP_ID =:shopId " +
            "AND WARE_HOUSE_TYPE_ID =:wareHouseTypeId AND PRODUCT_ID =:productId AND STATUS = 1 ", nativeQuery = true)
    Optional<StockTotal> getStockTotal(Long shopId, Long wareHouseTypeId, Long productId);

    @Query(value = "SELECT s FROM StockTotal s WHERE s.shopId =:shopId " +
            "AND s.wareHouseTypeId =:wareHouseTypeId AND s.productId IN :productIds AND s.status = 1 ")
    List<StockTotal> getStockTotal(Long shopId, Long wareHouseTypeId, List<Long> productIds);

    //kiểm kê
    @Query(value = "SELECT new vn.viettel.sale.service.dto.StockCountingDetailDTO(p.id, p.productCode, p.productName, gcat.productInfoName, " +
            " cat.productInfoName, s.quantity, p.uom1, p.uom2, p.convFact) " +
            " FROM Product p " +
            " LEFT JOIN ProductInfo gcat ON p.groupCatId = gcat.id and gcat.type = 6 and gcat.status = 1 " +
            " LEFT JOIN ProductInfo cat ON p.catId = gcat.id and gcat.type = 1 and gcat.status = 1 " +
            " JOIN StockTotal s ON s.productId = p.id AND s.wareHouseTypeId = :wareHouseTypeId AND s.shopId =:shopId AND s.status = 1 " +
            " WHERE (:searchKeywords is null OR p.productName LIKE %:searchKeywords% OR p.productCode LIKE %:searchKeywords%) " +
            " ORDER BY p.productCode asc")
    List<StockCountingDetailDTO> getStockCountingDetail(Long shopId, Long wareHouseTypeId, String searchKeywords);
}
