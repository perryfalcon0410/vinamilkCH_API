package vn.viettel.sale.specification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.*;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.List;
import java.util.Locale;

public class ProductSpecification {

    public static Specification<Product> hasCodeOrName(String keyWord) {
        return (root, query, criteriaBuilder) -> {
            if (keyWord == null) {
                return criteriaBuilder.conjunction();
            }
            String nameLowerCase = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Product_.productName), "%" + keyWord.trim() + "%"),
                    criteriaBuilder.like(root.get(Product_.productNameText), "%" + nameLowerCase.toUpperCase().trim() + "%"),
                    criteriaBuilder.like(root.get(Product_.productCode), "%" + keyWord.toUpperCase().trim() + "%")
            );
        };
    }
    public static Specification<Product> hasProductInfo(Long infoId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(infoId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                criteriaBuilder.in(root.get(Product_.catId) ).value(infoId),
                criteriaBuilder.in(root.get(Product_.subCatId) ).value(infoId),
                criteriaBuilder.in(root.get(Product_.brandId) ).value(infoId),
                criteriaBuilder.in(root.get(Product_.packingId) ).value(infoId)
            );

        };
    }
    public static Specification<Product> hasProductCode(String productCode) {
        return (root, query, criteriaBuilder) -> {
            if (productCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(Product_.productCode)), "%" + productCode.toUpperCase().trim() + "%");
        };
    }
    public static Specification<Product> hasProductName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.upper(root.get(Product_.productName)), "%" + productName.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get(Product_.productNameText)), "%" + VNCharacterUtils.removeAccent(productName.toUpperCase() + "%"))
            );
        };
    }

   public static Specification<Product> hasCatId(Long catId) {
       return (root, criteriaQuery, criteriaBuilder) -> {
           if(catId == null) {
               return criteriaBuilder.conjunction();
           }
           return criteriaBuilder.equal(root.get(Product_.catId), catId);
       };
   }
    public static Specification<Product> hasStatus() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.status), 1);
    }

    public static Specification<Product> hasStockTotal(boolean hasStockTotal, Long shopId) {

        return (root, query, criteriaBuilder) -> {
            if (hasStockTotal == false)
                return criteriaBuilder.conjunction();

            Subquery<StockTotal> subQuery = query.subquery(StockTotal.class);
            Root<StockTotal> subRoot = subQuery.from(StockTotal.class);
            Predicate predicate1 = criteriaBuilder.equal(subRoot.get(StockTotal_.productId), root.get("id"));
            Predicate predicate2 = criteriaBuilder.greaterThan(subRoot.get(StockTotal_.quantity), 0);

            subQuery.select(subRoot).where(predicate1, predicate2);
            return criteriaBuilder.exists(subQuery);
        };
    }
}
