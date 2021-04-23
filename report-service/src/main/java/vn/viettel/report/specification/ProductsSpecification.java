//package vn.viettel.report.specification;
//
//import org.springframework.data.jpa.domain.Specification;
//import vn.viettel.core.db.entity.common.Product;
//import vn.viettel.core.db.entity.common.Product_;
//import vn.viettel.core.db.entity.stock.PoTrans;
//import vn.viettel.core.db.entity.stock.PoTrans_;
//import vn.viettel.core.db.entity.stock.StockBorrowingTrans;
//import vn.viettel.core.db.entity.stock.StockBorrowingTrans_;
//
//import java.util.List;
//
//public class ProductsSpecification {
//    public static Specification<Product> hasProductCode(List<String> productCode) {
//        return (root, query, criteriaBuilder) -> {
//            if (productCode == null) {
//                return criteriaBuilder.conjunction();
//            }
//            return root.get(Product_.productCode).in(productCode);
//        };
//    }
//    public static Specification<Product> hasProductName(String productName) {
//        return (root, query, criteriaBuilder) -> {
//            if (productName == null) {
//                return criteriaBuilder.conjunction();
//            }
//            return criteriaBuilder.like(root.get(Product_.productName), "%" + productName + "%");
//        };
//    }
//    public static Specification<Product> hasCatId(Long catId) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Product_.catId), catId);
//    }
//}
