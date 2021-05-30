package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.entities.ExchangeTransDetail;
import vn.viettel.sale.entities.ExchangeTransDetail_;
import vn.viettel.sale.entities.ExchangeTrans_;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
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

    public static Specification<ExchangeTrans> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> {
            if (sFromDate == null || sToDate == null) {
                return criteriaBuilder.conjunction();
            }
            Timestamp tsFromDate = DateUtils.convertFromDate(sFromDate);
            Timestamp tsToDate = DateUtils.convertToDate(sToDate);

            if(tsFromDate == null && tsToDate != null)
                return criteriaBuilder.lessThanOrEqualTo(root.get(ExchangeTrans_.transDate), tsToDate);

            if(tsFromDate != null && tsToDate == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ExchangeTrans_.transDate), tsFromDate);

            return criteriaBuilder.between(root.get(ExchangeTrans_.transDate), tsFromDate, tsToDate);
        };
    }

    public static Specification<ExchangeTrans> hasDetail() {
        return (root, query, criteriaBuilder) -> {
            Subquery<ExchangeTransDetail> subQuery = query.subquery(ExchangeTransDetail.class);
            Root<ExchangeTransDetail> subRoot = subQuery.from(ExchangeTransDetail.class);
            Predicate idPredicate = criteriaBuilder.equal(subRoot.get(ExchangeTransDetail_.transId), root.get("id"));

            subQuery.select(subRoot).where(idPredicate);
            return criteriaBuilder.exists(subQuery);
        };
    }
}
