package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.OnlineOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OnlineOrderDetailRepository extends BaseRepository<OnlineOrderDetail> {

    List<OnlineOrderDetail> findByOnlineOrderId(Long id);

    @Modifying
    @Query(value = "Insert into ONLINE_ORDER_DETAIL (SHOP_ID, ONLINE_ORDER_ID, SKU, PRODUCT_NAME, QUANTITY, ORIGINAL_PRICE, RETAILS_PRICE, " +
            " LINE_VALUE, CHARACTER1_NAME, CHARACTER1_VALUE, CHARACTER2_NAME, CHARACTER2_VALUE, CHARACTER3_NAME, CHARACTER3_VALUE, PROMOTION_NAME, CREATED_AT) " +
            "VALUES (:shopId, :onlineOrderId, :sku, :productName, TO_NUMBER(:quantity), TO_NUMBER(:originalPrice), TO_NUMBER(:retailsPrice) ," +
            " TO_NUMBER(:lineValue), :character1Name, TO_NUMBER(:character1Value), :character2Name, TO_NUMBER(:character2Value), :character3Name, TO_NUMBER(:character3Value), :promotionName , :createdAt)", nativeQuery = true)
    int schedulerInsert(Long shopId, Long onlineOrderId, String sku,  String productName, Integer quantity, Float originalPrice, Float retailsPrice,
                Float lineValue, String character1Name, String character1Value, String character2Name, String character2Value, String character3Name, String character3Value, String promotionName, LocalDateTime createdAt);
}
