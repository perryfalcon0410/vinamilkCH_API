package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.entities.ExchangeTrans_;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ExchangeTransSpecification {

    public static Specification<ExchangeTrans> hasShopId(Long shopId) {

        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ExchangeTrans_.shopId), shopId);
        };
    }

    public static Specification<ExchangeTrans> hasWareHouseType(Long type) {

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ExchangeTrans_.wareHouseTypeId), type);
    }


    public static Specification<ExchangeTrans> hasTranCode(String tranCode) {

        return (root, query, criteriaBuilder) -> {
            if (tranCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(ExchangeTrans_.transCode)),
                    "%" + tranCode.toUpperCase() + "%");
        };
    }

    public static Specification<ExchangeTrans> hasReasonId(Long reasonId) {

        return (root, query, criteriaBuilder) -> {
            if (reasonId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ExchangeTrans_.reasonId), reasonId);
        };
    }

    public static Specification<ExchangeTrans> hasStatus() {

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ExchangeTrans_.status), 1);
    }

    public static Specification<ExchangeTrans> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            Timestamp tsFromDate = DateUtils.convertFromDate(fromDate);
            Timestamp tsToDate = DateUtils.convertToDate(toDate);

            if (tsFromDate == null && tsToDate == null) return criteriaBuilder.conjunction();

            if(tsFromDate == null && tsToDate != null)
                return criteriaBuilder.lessThanOrEqualTo(root.get(ExchangeTrans_.transDate), tsToDate);

            if(tsFromDate != null && tsToDate == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ExchangeTrans_.transDate), tsFromDate);

            return criteriaBuilder.between(root.get(ExchangeTrans_.transDate), tsFromDate, tsToDate);
        };
    }
}
