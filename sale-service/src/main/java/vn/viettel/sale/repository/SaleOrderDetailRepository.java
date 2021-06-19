package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.ProductDetailDTO;
import vn.viettel.sale.service.dto.RedInvoiceDataDTO;

import java.math.BigDecimal;
import java.util.List;

public interface SaleOrderDetailRepository extends BaseRepository<SaleOrderDetail> {

    @Query(value = "SELECT sd FROM SaleOrderDetail sd WHERE ( :isFreeItem IS NULL OR sd.isFreeItem = :isFreeItem ) AND sd.saleOrderId = :saleOrderId")
    List<SaleOrderDetail> findSaleOrderDetail(Long saleOrderId, Boolean isFreeItem);

    /*
    lấy thông tin ProductDetailDTO
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.RedInvoiceDataDTO (so.id, so.note, p.id, p.productCode, p.productName, p.uom1, p.uom2, price.vat," +
            " p.groupVat, soDtl.quantity, soDtl.price, soDtl.priceNotVat, soDtl.amount ) " +
            "FROM Product p " +
            "   JOIN Price price ON price.productId = p.id AND price.status = 1 AND price.priceType = 1 " +
            "   JOIN SaleOrderDetail soDtl ON soDtl.productId = p.id AND soDtl.isFreeItem = false " +
            "   JOIN SaleOrder so ON soDtl.saleOrderId = so.id " +
            "   WHERE so.orderNumber IN (:orderNumbers) AND p.status = 1 AND (:customerId IS NULL OR so.customerId = :customerId) ")
    List<RedInvoiceDataDTO> findRedInvoiceDataDTO(Long customerId, List<String> orderNumbers);
}
