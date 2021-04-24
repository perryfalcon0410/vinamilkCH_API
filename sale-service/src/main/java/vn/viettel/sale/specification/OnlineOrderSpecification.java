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

    public static Specification<OnlineOrder> hasFromDateToDate(Date sFromDate, Date sToDate) {
        Timestamp tsFromDate =new Timestamp(sFromDate.getTime());
        LocalDateTime localDateTime = LocalDateTime.of(sToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Timestamp tsToDate = Timestamp.valueOf(localDateTime);

        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(OnlineOrder_.createdAt), tsFromDate, tsToDate);
    }

}
