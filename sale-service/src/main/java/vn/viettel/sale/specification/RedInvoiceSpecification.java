package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.sale.entities.RedInvoice_;

import java.util.Date;
import java.util.List;

public class RedInvoiceSpecification {
    public static Specification<RedInvoice> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(RedInvoice_.createdAt), sFromDate, sToDate);
    }

    public static Specification<RedInvoice> hasCustomerId(List<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null) {
                return criteriaBuilder.conjunction();
            }
            return root.get(RedInvoice_.customerId).in(ids);
        };
    }

    public static Specification<RedInvoice> hasShopId(Long shopId) {
        return (root, query, criteriaBuilder) -> {
            if (shopId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(RedInvoice_.shopId), shopId);
        };
    }

    public static Specification<RedInvoice> hasInvoiceNumber(String invoiceNumber) {
        return (root, query, criteriaBuilder) -> {
            if (invoiceNumber == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get(RedInvoice_.invoiceNumber), "%" + invoiceNumber + "%");
        };
    }
}
