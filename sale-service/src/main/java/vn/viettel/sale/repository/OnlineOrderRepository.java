package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.core.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OnlineOrderRepository extends BaseRepository<OnlineOrder>, JpaSpecificationExecutor<OnlineOrder> {
    OnlineOrder findByOrderNumber(String orderNumber);

    @Query(value = "SELECT onl FROM OnlineOrder onl WHERE onl.synStatus = 1 AND onl.vnmSynStatus = 0 AND onl.shopId =:shopId")
    List<OnlineOrder> findOnlineOrderExportXml(Long shopId);

    @Query(value = "SELECT DISTINCT shopId FROM OnlineOrder WHERE synStatus = 1 AND vnmSynStatus = 0")
    List<Long> findALLShopId();

    @Modifying()
    @Query(value = "Update OnlineOrder SET vnmSynStatus = :vnmSynStatus , vnmSynTime = :vnmSynTime where id =:id")
    int schedulerUpdate(Integer vnmSynStatus, LocalDateTime vnmSynTime, Long id);

    @Modifying()
    @Query(value = "Update OnlineOrder SET synStatus = :synStatus , orderNumber =:orderNumber Where id =:id")
    int schedulerCancel(Integer synStatus, String orderNumber, Long id);

    @Modifying
    @Query(value = "Insert into ONLINE_ORDER (SHOP_ID, SYN_STATUS, SOURCE_NAME, ORDER_ID, ORDER_NUMBER, TOTAL_LINE_VALUE " +
            ", DISCOUNT_CODE, DISCOUNT_VALUE, CUSTOMER_NAME, CUSTOMER_PHONE, CUSTOMER_ADDRESS, SHIPPING_ADDRESS, CUSTOMER_BIRTHDAY, ORDER_STATUS, VNM_SYN_STATUS, NOTE, CREATED_AT) " +
            "VALUES (:shopId, :sysStatus, :soureName, :orderId, :orderNumber, :totalLineValue, :discountCode, TO_NUMBER(:discountValue)" +
            ", :customerName, :customerPhone, :cusAddress, :shippingAddress, :customerDOB, :orderStatus, :vnmSynStatus, :note, :createdAt)", nativeQuery = true)
    int schedulerInsert( Long shopId, Integer sysStatus, String soureName, Long orderId, String orderNumber,
        Float totalLineValue, String discountCode, Float discountValue, String customerName, String customerPhone, String cusAddress,
                                    String shippingAddress, LocalDateTime customerDOB, String orderStatus, Integer vnmSynStatus, String note, LocalDateTime createdAt);

    OnlineOrder findFirstByOrderByIdDesc();
}
