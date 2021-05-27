package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.entities.ExchangeTrans_;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ExchangeTransSpecification {
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

    public static Specification<ExchangeTrans> hasFromDate(Date sFromDate) {

        return (root, query, criteriaBuilder) -> {
            if (sFromDate == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDate localDate = sFromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            return criteriaBuilder.greaterThanOrEqualTo(root.get(ExchangeTrans_.transDate), date);
        };
    }

    public static Specification<ExchangeTrans> hasToDate(Date sToDate) {
        return (root, query, criteriaBuilder) -> {
            if (sToDate == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDate localDate = sToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            return criteriaBuilder.lessThanOrEqualTo(root.get(ExchangeTrans_.transDate), date);
        };
    }

    public static Specification<ExchangeTrans> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> {
            if (sFromDate == null || sToDate == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDate localDate = sFromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
            Date fromDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            LocalDate localDate2 = sToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
            Date toDate = Date.from(localDate2.atStartOfDay(ZoneId.systemDefault()).toInstant());

            return criteriaBuilder.between(root.get(ExchangeTrans_.transDate), fromDate, toDate);
        };
    }
}
