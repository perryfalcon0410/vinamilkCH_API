package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.service.dto.ComboProductDetailDTO;

import java.util.List;

public interface ComboProductRepository extends BaseRepository<ComboProduct>, JpaSpecificationExecutor<ComboProduct> {
    @Query("SELECT NEW vn.viettel.sale.service.dto.ComboProductDetailDTO ( cbPro.id, p.id, p.productCode, cbPro.numProduct, " +
            " pDtl.id, pDtl.productName, pDtl.productCode, price.price, price.priceNotVat, cbDtl.factor ) " +
            "FROM Product p " +
            "   JOIN ComboProduct cbPro ON cbPro.id = p.comboProductId AND cbPro.status = 1" +
            "   JOIN ComboProductDetail cbDtl ON cbPro.id = cbDtl.comboProductId AND cbDtl.status = 1" +
            "   JOIN Product pDtl ON pDtl.id = cbDtl.productId AND cbDtl.status = 1" +
            "   JOIN Price price ON price.productId = cbDtl.productId AND price.customerTypeId =:customerTypeId AND price.status = 1 AND price.priceType = -1 " +
            "   WHERE p.id IN :productIds AND p.status = 1  AND p.isCombo = true ")
    List<ComboProductDetailDTO> findComboProduct(Long customerTypeId, List<Long> productIds);
}

