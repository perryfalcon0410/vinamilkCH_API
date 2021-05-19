package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.Product_;
import vn.viettel.sale.entities.StockBorrowingTrans_;

import java.util.List;
import java.util.Locale;

public class ProductSpecification {

    public  static  Specification<Product> hasStatus(Integer status) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(Product_.status), status);
        };
    }

    public static Specification<Product> hasCodeOrName(String keyWord) {
        return (root, query, criteriaBuilder) -> {
            if (keyWord == null) {
                return criteriaBuilder.conjunction();
            }
            String nameLowerCase = VNCharacterUtils.removeAccent(keyWord).toUpperCase(Locale.ROOT);
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Product_.productName), "%" + keyWord + "%"),
                    criteriaBuilder.like(root.get(Product_.productNameText), "%" + nameLowerCase + "%"),
                    criteriaBuilder.like(root.get(Product_.productCode), "%" + keyWord.toUpperCase() + "%")
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
    public static Specification<Product> hasProductCode(String[] productCode) {
        return (root, query, criteriaBuilder) -> {
            if (productCode == null) {
                return criteriaBuilder.conjunction();
            }
            return root.get(Product_.productCode).in(productCode);
        };
    }
    public static Specification<Product> hasProductName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(Product_.productName), "%" + productName + "%");
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
}
