package vn.viettel.saleservice.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.db.entity.ReceiptOnline_;

import java.util.Date;

public class ReceiptOnlineSpecification {

    public static Specification<ReceiptOnline> hasCode(String receiptCode) {

        return (root, query, criteriaBuilder) -> {
            if (receiptCode == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ReceiptOnline_.receiptCode), receiptCode);
        };
    }

    public static Specification<ReceiptOnline> hasStatus(Long status) {

        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(ReceiptOnline_.status), status);
        };
    }

    public static Specification<ReceiptOnline> hasFromDateToDate(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(ReceiptOnline_.createdAt), fromDate, toDate);
    }
}
