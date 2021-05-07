package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.entities.OnlineOrder_;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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

    public static Specification<OnlineOrder> hasDeletedAtIsNull() {

        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(OnlineOrder_.deletedAt));
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

         return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(OnlineOrder_.orderNumber), "%" + searchKeyword + "%");
    }

    public static Specification<OnlineOrder> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) return criteriaBuilder.conjunction();

            Timestamp tsFromDate = null;
            Timestamp tsToDate = null;
            if(fromDate != null) tsFromDate = new Timestamp(fromDate.getTime());
            if(toDate != null){
                LocalDateTime localDateTime = LocalDateTime
                        .of(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
                tsToDate = Timestamp.valueOf(localDateTime);
            }

            if(fromDate == null && toDate != null)
                return criteriaBuilder.lessThanOrEqualTo(root.get(OnlineOrder_.createdAt), tsToDate);

            if(fromDate != null && toDate == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get(OnlineOrder_.createdAt), tsFromDate);

            return criteriaBuilder.between(root.get(OnlineOrder_.createdAt), tsFromDate, tsToDate);
        };
    }

}
