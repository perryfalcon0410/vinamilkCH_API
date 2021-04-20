package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.OnlineOrder;

import java.util.Date;

public class OnlineOrderSpecification {

    public static Specification<OnlineOrder> hasShopId(Long shopId) {

        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            /*return criteriaBuilder.equal(root.get(OnlineOrder_.shopId), shopId);*/
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<OnlineOrder> hasDeletedAtIsNull() {
       return  null;
        /*return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(OnlineOrder_.deletedAt));*/
    }

    public static Specification<OnlineOrder> hasSynStatus(Integer synStatus) {

        return (root, query, criteriaBuilder) -> {
            if (synStatus == null) {
                return criteriaBuilder.conjunction();
            }
            /*return criteriaBuilder.equal(root.get(OnlineOrder_.synStatus), synStatus);*/
            return criteriaBuilder.conjunction();
        };
    }

    public  static  Specification<OnlineOrder> hasOrderNumber(String searchKeyword){
       return null;
        /* return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(OnlineOrder_.orderNumber), "%" + searchKeyword + "%");*/
    }

    public static Specification<OnlineOrder> hasFromDateToDate(Date sFromDate, Date sToDate) {
      return null;
       /* return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(OnlineOrder_.createdAt), sFromDate, sToDate);*/
    }

}
