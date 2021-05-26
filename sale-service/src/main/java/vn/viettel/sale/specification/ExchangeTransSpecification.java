package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.entities.ExchangeTrans_;

import java.util.Date;

public class ExchangeTransSpecification {
    public static Specification<ExchangeTrans> hasTranCode(String tranCode) {

        return (root, query, criteriaBuilder) -> {
            if (tranCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(ExchangeTrans_.transCode), tranCode);

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

    public static Specification<ExchangeTrans> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(ExchangeTrans_.createdAt), sFromDate, sToDate);
    }
}
