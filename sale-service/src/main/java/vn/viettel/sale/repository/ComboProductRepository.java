package vn.viettel.sale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.service.dto.ComboProductDetailDTO;

public interface ComboProductRepository extends BaseRepository<ComboProduct>, JpaSpecificationExecutor<ComboProduct> {
    @Query("SELECT NEW vn.viettel.sale.service.dto.ComboProductDetailDTO ( cbPro.id, p.id, p.productCode, cbPro.numProduct, " +
            " pDtl.id, pDtl.productName, pDtl.productCode, cbDtl.factor ) " +
            "FROM Product p " +
            "   JOIN ComboProduct cbPro ON cbPro.id = p.comboProductId AND cbPro.status = 1" +
            "   JOIN ComboProductDetail cbDtl ON cbPro.id = cbDtl.comboProductId AND cbDtl.status = 1" +
            "   JOIN Product pDtl ON pDtl.id = cbDtl.productId AND cbDtl.status = 1" +
            "   WHERE p.id IN :productIds AND p.status = 1  AND p.isCombo = true ")
    List<ComboProductDetailDTO> findComboProduct(List<Long> productIds);

    @Query("SELECT p FROM ComboProduct p WHERE p.id IN (:productIds) AND (:status IS null or p.status = :status )")
    List<ComboProduct> findComboProducts(List<Long> productIds, Integer status);
}

