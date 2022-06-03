package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.*;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;


public class ReceiptSpecification {

    public static Specification<PoTrans> hasTypeImport() {
          return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.type), 1);
    }
    public static Specification<PoTrans> hasShopId(Long shopId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.shopId), shopId);
    }
    public static Specification<PoTrans> hasStatus() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoTrans_.status), 1);
    }
    public static Specification<PoTrans> hasFromDateToDate(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                 return criteriaBuilder.lessThanOrEqualTo(root.get(PoTrans_.transDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(PoTrans_.transDate), tsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(PoTrans_.transDate), tsFromDate, tsToDate);
        };
    }
    public static Specification<PoTrans> hasFromDateToDateRedInvoice(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
        return (root, query, criteriaBuilder) ->{
            if (fromDate == null && toDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(PoTrans_.orderDate), tsToDate);
            }
            if (toDate == null && fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(PoTrans_.orderDate), tsFromDate);
            }
            if(fromDate == null && toDate == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get(PoTrans_.orderDate), tsFromDate, tsToDate);
        };
    }
    public static Specification<PoTrans> hasTransCode(String transCode) {
        return (root, query, criteriaBuilder) -> {

            if (transCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.transCode)), "%" + transCode.toUpperCase() + "%");
        };
    }
    public static Specification<PoTrans> hasRedInvoiceNo(String redInvoiceNo) {
        return (root, query, criteriaBuilder) -> {
            if (redInvoiceNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.redInvoiceNo)), "%" + redInvoiceNo.toUpperCase() + "%");
        };
    }
    public static Specification<PoTrans> hasInternalNumber(String internalNumber) {
        return (root, query, criteriaBuilder) -> {
            if (internalNumber == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.internalNumber)), "%" + internalNumber.toUpperCase() + "%");
        };
    }
    public static Specification<PoTrans> hasPoCoNo(String poCoNo) {
        return (root, query, criteriaBuilder) -> {
            if (poCoNo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(PoTrans_.pocoNumber)), "%" + poCoNo.toUpperCase() + "%");
        };
    }

    public static Specification<PoTrans> hasGreaterDay(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(PoTrans_.orderDate),dateTime);
    }

    public static Specification<PoTrans> hasNotReturn() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<PoTransDetail> subRoot = subQuery.from(PoTransDetail.class);
            subQuery.groupBy(subRoot.get(PoTransDetail_.transId));
            Predicate idPredicate = criteriaBuilder.equal(subRoot.get(PoTransDetail_.transId), root.get("id"));
            subQuery.select(subRoot.get(PoTransDetail_.transId)).where(idPredicate);
            subQuery.having( criteriaBuilder.greaterThan(criteriaBuilder.sum(subRoot.get(PoTransDetail_.quantity)), criteriaBuilder.sum(subRoot.get(PoTransDetail_.returnAmount)) ) );

            return criteriaBuilder.exists(subQuery);
        };
    }
}