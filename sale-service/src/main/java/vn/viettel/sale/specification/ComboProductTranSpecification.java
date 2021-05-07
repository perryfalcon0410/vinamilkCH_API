package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.ComboProductTrans;
import vn.viettel.sale.entities.ComboProductTrans_;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class ComboProductTranSpecification {

    public static Specification<ComboProductTrans> hasShopId(Long shopId) {

        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ComboProductTrans_.shopId), shopId);
        };
    }

    public  static  Specification<ComboProductTrans> hasTransCode(String transCode){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(ComboProductTrans_.transCode), "%" + transCode + "%");
    }

    public static Specification<ComboProductTrans> hasTransType(Integer transType) {

        return (root, query, criteriaBuilder) -> {
            if (transType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ComboProductTrans_.transType), transType);
        };
    }

    public static Specification<ComboProductTrans> hasFromDateToDate(Date fromDate, Date toDate) {

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
                return criteriaBuilder.lessThanOrEqualTo(root.get(ComboProductTrans_.transDate), tsToDate);

            if(fromDate != null && toDate == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ComboProductTrans_.transDate), tsFromDate);

            return criteriaBuilder.between(root.get(ComboProductTrans_.transDate), tsFromDate, tsToDate);
        };

    }

}
