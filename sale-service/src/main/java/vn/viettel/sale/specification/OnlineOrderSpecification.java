package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.entities.OnlineOrder_;

import java.sql.Timestamp;
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

    public static Specification<OnlineOrder> hasSynStatus(Integer synStatus) {

        return (root, query, criteriaBuilder) -> {
            if (synStatus == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(OnlineOrder_.synStatus), synStatus);
        };
    }

    public  static  Specification<OnlineOrder> hasOrderNumber(String searchKeyword){
         return (root, query, criteriaBuilder) -> {
            if (searchKeyword == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(OnlineOrder_.orderNumber), "%" + searchKeyword.toUpperCase() + "%");
        };
    }

    public static Specification<OnlineOrder> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            Timestamp tsFromDate = DateUtils.convertFromDate(fromDate);
            Timestamp tsToDate = DateUtils.convertToDate(toDate);

            if (tsFromDate == null && tsToDate == null) return criteriaBuilder.conjunction();

            if(tsFromDate == null && tsToDate != null)
                return criteriaBuilder.lessThanOrEqualTo(root.get(OnlineOrder_.createdAt), tsToDate);

            if(tsFromDate != null && tsToDate == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get(OnlineOrder_.createdAt), tsFromDate);

            return criteriaBuilder.between(root.get(OnlineOrder_.createdAt), tsFromDate, tsToDate);
        };
    }

}
