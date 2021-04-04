package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.db.entity.sale.OnlineOrder_;

import java.util.Date;

public class OnlineOrderSpecification {

    public static Specification<OnlineOrder> hasShopId(Long shopId) {

        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(OnlineOrder_.shopId), shopId);
        };
    }

    public static Specification<OnlineOrder> hasSysStatus(Integer sysStatus) {

        return (root, query, criteriaBuilder) -> {
            if (sysStatus == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(OnlineOrder_.synStatus), sysStatus);
        };
    }

    public  static  Specification<OnlineOrder> hasOrderNumber(String searchKeyword){
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get(OnlineOrder_.orderNumber), "%" + searchKeyword + "%");
        };
    }

    public static Specification<OnlineOrder> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(OnlineOrder_.createdAt), sFromDate, sToDate);
    }

}
