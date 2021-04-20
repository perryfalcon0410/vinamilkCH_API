package vn.viettel.sale.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.sale.entities.RedInvoice_;

import java.util.Date;

public class RedInvoiceSpefication {
    public static Specification<RedInvoice> hasFromDateToDate(Date sFromDate, Date sToDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(RedInvoice_.createdAt), sFromDate, sToDate);
    }

    public static Specification<RedInvoice> hasCustomerId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(RedInvoice_.customerId), id);
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
